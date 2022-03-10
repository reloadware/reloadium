from __future__ import print_function

import io
import gzip

from datetime import datetime, timedelta

from reloadium.vendored.sentry_sdk.utils import Dsn, logger, capture_internal_exceptions, json_dumps
from reloadium.vendored.sentry_sdk.worker import BackgroundWorker
from reloadium.vendored.sentry_sdk.envelope import Envelope

from reloadium.vendored.sentry_sdk._types import MYPY

from urllib import request

if MYPY:
    from typing import Any
    from typing import Callable
    from typing import Dict
    from typing import Iterable
    from typing import Optional
    from typing import Tuple
    from typing import Type
    from typing import Union

    from reloadium.vendored.sentry_sdk._types import Event, EndpointType

    DataCategory = Optional[str]

try:
    from urllib.request import getproxies
except ImportError:
    from urllib import getproxies  # type: ignore


class Transport(object):
    """Baseclass for all transports.

    A transport is used to send an event to sentry.
    """

    parsed_dsn = None  # type: Optional[Dsn]

    def __init__(
        self, options=None  # type: Optional[Dict[str, Any]]
    ):
        # type: (...) -> None
        self.options = options
        if options and options["dsn"] is not None and options["dsn"]:
            self.parsed_dsn = Dsn(options["dsn"])
        else:
            self.parsed_dsn = None

    def capture_event(
        self, event  # type: Event
    ):
        # type: (...) -> None
        """
        This gets invoked with the event dictionary when an event should
        be sent to sentry.
        """
        raise NotImplementedError()

    def capture_envelope(
        self, envelope  # type: Envelope
    ):
        # type: (...) -> None
        """
        Send an envelope to Sentry.

        Envelopes are a data container format that can hold any type of data
        submitted to Sentry. We use it for transactions and sessions, but
        regular "error" events should go through `capture_event` for backwards
        compat.
        """
        raise NotImplementedError()

    def flush(
        self,
        timeout,  # type: float
        callback=None,  # type: Optional[Any]
    ):
        # type: (...) -> None
        """Wait `timeout` seconds for the current events to be sent out."""
        pass

    def kill(self):
        # type: () -> None
        """Forcefully kills the transport."""
        pass

    def __del__(self):
        # type: () -> None
        try:
            self.kill()
        except Exception:
            pass


class HttpTransport(Transport):
    """The default HTTP transport."""

    def __init__(
        self, options  # type: Dict[str, Any]
    ):
        # type: (...) -> None
        from reloadium.vendored.sentry_sdk.consts import VERSION

        Transport.__init__(self, options)
        assert self.parsed_dsn is not None
        self.options = options
        self._worker = BackgroundWorker(queue_size=options["transport_queue_size"])
        self._auth = self.parsed_dsn.to_auth("sentry.python/%s" % VERSION)
        self._disabled_until = {}  # type: Dict[DataCategory, datetime]

        from reloadium.vendored.sentry_sdk import Hub

        self.hub_cls = Hub

    def _send_request(
        self,
        body,  # type: bytes
        headers,  # type: Dict[str, str]
        endpoint_type="store",  # type: EndpointType
    ):
        # type: (...) -> None
        headers.update(
            {
                "User-Agent": str(self._auth.client),
                "X-Sentry-Auth": str(self._auth.to_header()),
            }
        )
        try:
            req = request.Request(url=str(self._auth.get_api_url(endpoint_type)), data=body, headers=headers)
            response = request.urlopen(req)
        except Exception:
            self.on_dropped_event("network")
            raise

    def on_dropped_event(self, reason):
        # type: (str) -> None
        pass

    def _check_disabled(self, category):
        # type: (str) -> bool
        def _disabled(bucket):
            # type: (Any) -> bool
            ts = self._disabled_until.get(bucket)
            return ts is not None and ts > datetime.utcnow()

        return _disabled(category) or _disabled(None)

    def _send_event(
        self, event  # type: Event
    ):
        # type: (...) -> None

        if self._check_disabled("error"):
            self.on_dropped_event("self_rate_limits")
            return None

        body = io.BytesIO()
        with gzip.GzipFile(fileobj=body, mode="w") as f:
            f.write(json_dumps(event))

        assert self.parsed_dsn is not None
        logger.debug(
            "Sending event, type:%s level:%s event_id:%s project:%s host:%s"
            % (
                event.get("type") or "null",
                event.get("level") or "null",
                event.get("event_id") or "null",
                self.parsed_dsn.project_id,
                self.parsed_dsn.host,
            )
        )
        self._send_request(
            body.getvalue(),
            headers={"Content-Type": "application/json", "Content-Encoding": "gzip"},
        )
        return None

    def _send_envelope(
        self, envelope  # type: Envelope
    ):
        # type: (...) -> None

        # remove all items from the envelope which are over quota
        envelope.items[:] = [
            x for x in envelope.items if not self._check_disabled(x.data_category)
        ]
        if not envelope.items:
            return None

        body = io.BytesIO()
        with gzip.GzipFile(fileobj=body, mode="w") as f:
            envelope.serialize_into(f)

        assert self.parsed_dsn is not None
        logger.debug(
            "Sending envelope [%s] project:%s host:%s",
            envelope.description,
            self.parsed_dsn.project_id,
            self.parsed_dsn.host,
        )
        self._send_request(
            body.getvalue(),
            headers={
                "Content-Type": "application/x-sentry-envelope",
                "Content-Encoding": "gzip",
            },
            endpoint_type="envelope",
        )
        return None

    def _in_no_proxy(self, parsed_dsn):
        # type: (Dsn) -> bool
        no_proxy = getproxies().get("no")
        if not no_proxy:
            return False
        for host in no_proxy.split(","):
            host = host.strip()
            if parsed_dsn.host.endswith(host) or parsed_dsn.netloc.endswith(host):
                return True
        return False

    def capture_event(
        self, event  # type: Event
    ):
        # type: (...) -> None
        hub = self.hub_cls.current

        def send_event_wrapper():
            # type: () -> None
            with hub:
                with capture_internal_exceptions():
                    self._send_event(event)

        if not self._worker.submit(send_event_wrapper):
            self.on_dropped_event("full_queue")

    def capture_envelope(
        self, envelope  # type: Envelope
    ):
        # type: (...) -> None
        hub = self.hub_cls.current

        def send_envelope_wrapper():
            # type: () -> None
            with hub:
                with capture_internal_exceptions():
                    self._send_envelope(envelope)

        if not self._worker.submit(send_envelope_wrapper):
            self.on_dropped_event("full_queue")

    def flush(
        self,
        timeout,  # type: float
        callback=None,  # type: Optional[Any]
    ):
        # type: (...) -> None
        logger.debug("Flushing HTTP transport")
        if timeout > 0:
            self._worker.flush(timeout, callback)

    def kill(self):
        # type: () -> None
        logger.debug("Killing HTTP transport")
        self._worker.kill()


class _FunctionTransport(Transport):
    def __init__(
        self, func  # type: Callable[[Event], None]
    ):
        # type: (...) -> None
        Transport.__init__(self)
        self._func = func

    def capture_event(
        self, event  # type: Event
    ):
        # type: (...) -> None
        self._func(event)
        return None


def make_transport(options):
    # type: (Dict[str, Any]) -> Optional[Transport]
    ref_transport = options["transport"]

    # If no transport is given, we use the http transport class
    if ref_transport is None:
        transport_cls = HttpTransport  # type: Type[Transport]
    elif isinstance(ref_transport, Transport):
        return ref_transport
    elif isinstance(ref_transport, type) and issubclass(ref_transport, Transport):
        transport_cls = ref_transport
    elif callable(ref_transport):
        return _FunctionTransport(ref_transport)  # type: ignore

    # if a transport class is given only instantiate it if the dsn is not
    # empty or None
    if options["dsn"]:
        return transport_cls(options)

    return None
