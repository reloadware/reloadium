import re
import uuid
import contextlib
import math
import random
import time

from datetime import datetime, timedelta
from numbers import Real

import reloadium.vendored.sentry_sdk

from reloadium.vendored.sentry_sdk.utils import (
    capture_internal_exceptions,
    logger,
    to_string,
)
from reloadium.vendored.sentry_sdk._compat import PY2
from reloadium.vendored.sentry_sdk._types import MYPY

if PY2:
    from collections import Mapping
else:
    from collections.abc import Mapping

if MYPY:
    import typing

    from typing import Generator
    from typing import Optional
    from typing import Any
    from typing import Dict
    from typing import List
    from typing import Tuple

    from reloadium.vendored.sentry_sdk._types import SamplingContext

_traceparent_header_format_re = re.compile(
    "^[ \t]*"  # whitespace
    "([0-9a-f]{32})?"  # trace_id
    "-?([0-9a-f]{16})?"  # span_id
    "-?([01])?"  # sampled
    "[ \t]*$"  # whitespace
)


class EnvironHeaders(Mapping):  # type: ignore
    def __init__(
        self,
        environ,  # type: typing.Mapping[str, str]
        prefix="HTTP_",  # type: str
    ):
        # type: (...) -> None
        self.environ = environ
        self.prefix = prefix

    def __getitem__(self, key):
        # type: (str) -> Optional[Any]
        return self.environ[self.prefix + key.replace("-", "_").upper()]

    def __len__(self):
        # type: () -> int
        return sum(1 for _ in iter(self))

    def __iter__(self):
        # type: () -> Generator[str, None, None]
        for k in self.environ:
            if not isinstance(k, str):
                continue

            k = k.replace("-", "_").upper()
            if not k.startswith(self.prefix):
                continue

            yield k[len(self.prefix) :]


class _SpanRecorder(object):
    """Limits the number of spans recorded in a transaction."""

    __slots__ = ("maxlen", "spans")

    def __init__(self, maxlen):
        # type: (int) -> None
        # FIXME: this is `maxlen - 1` only to preserve historical behavior
        # enforced by tests.
        # Either this should be changed to `maxlen` or the JS SDK implementation
        # should be changed to match a consistent interpretation of what maxlen
        # limits: either transaction+spans or only child spans.
        self.maxlen = maxlen - 1
        self.spans = []  # type: List[Span]

    def add(self, span):
        # type: (Span) -> None
        if len(self.spans) > self.maxlen:
            span._span_recorder = None
        else:
            self.spans.append(span)


class Span(object):
    __slots__ = (
        "trace_id",
        "span_id",
        "parent_span_id",
        "same_process_as_parent",
        "sampled",
        "op",
        "description",
        "start_timestamp",
        "_start_timestamp_monotonic",
        "status",
        "timestamp",
        "_tags",
        "_data",
        "_span_recorder",
        "hub",
        "_context_manager_state",
        # TODO: rename this "transaction" once we fully and truly deprecate the
        # old "transaction" attribute (which was actually the transaction name)?
        "_containing_transaction",
    )

    def __new__(cls, **kwargs):
        # type: (**Any) -> Any
        """
        Backwards-compatible implementation of Span and Transaction
        creation.
        """

        # TODO: consider removing this in a future release.
        # This is for backwards compatibility with releases before Transaction
        # existed, to allow for a smoother transition.
        if "transaction" in kwargs:
            return object.__new__(Transaction)
        return object.__new__(cls)

    def __init__(
        self,
        trace_id=None,  # type: Optional[str]
        span_id=None,  # type: Optional[str]
        parent_span_id=None,  # type: Optional[str]
        same_process_as_parent=True,  # type: bool
        sampled=None,  # type: Optional[bool]
        op=None,  # type: Optional[str]
        description=None,  # type: Optional[str]
        hub=None,  # type: Optional[sentry_sdk.Hub]
        status=None,  # type: Optional[str]
        transaction=None,  # type: Optional[str] # deprecated
    ):
        # type: (...) -> None
        self.trace_id = trace_id or uuid.uuid4().hex
        self.span_id = span_id or uuid.uuid4().hex[16:]
        self.parent_span_id = parent_span_id
        self.same_process_as_parent = same_process_as_parent
        self.sampled = sampled
        self.op = op
        self.description = description
        self.status = status
        self.hub = hub
        self._tags = {}  # type: Dict[str, str]
        self._data = {}  # type: Dict[str, Any]
        self.start_timestamp = datetime.utcnow()
        try:
            # TODO: For Python 3.7+, we could use a clock with ns resolution:
            # self._start_timestamp_monotonic = time.perf_counter_ns()

            # Python 3.3+
            self._start_timestamp_monotonic = time.perf_counter()
        except AttributeError:
            pass

        #: End timestamp of span
        self.timestamp = None  # type: Optional[datetime]

        self._span_recorder = None  # type: Optional[_SpanRecorder]
        self._containing_transaction = None  # type: Optional[Transaction]

    def init_span_recorder(self, maxlen):
        # type: (int) -> None
        if self._span_recorder is None:
            self._span_recorder = _SpanRecorder(maxlen)
        self._span_recorder.add(self)

    def __repr__(self):
        # type: () -> str
        return "<%s(op=%r, description:%r, trace_id=%r, span_id=%r, parent_span_id=%r, sampled=%r)>" % (
            self.__class__.__name__,
            self.op,
            self.description,
            self.trace_id,
            self.span_id,
            self.parent_span_id,
            self.sampled,
        )

    def __enter__(self):
        # type: () -> Span
        hub = self.hub or sentry_sdk.Hub.current

        _, scope = hub._stack[-1]
        old_span = scope.span
        scope.span = self
        self._context_manager_state = (hub, scope, old_span)
        return self

    def __exit__(self, ty, value, tb):
        # type: (Optional[Any], Optional[Any], Optional[Any]) -> None
        if value is not None:
            self.set_status("internal_error")

        hub, scope, old_span = self._context_manager_state
        del self._context_manager_state

        self.finish(hub)
        scope.span = old_span

    def start_child(self, **kwargs):
        # type: (**Any) -> Span
        """
        Start a sub-span from the current span or transaction.

        Takes the same arguments as the initializer of :py:class:`Span`. The
        trace id, sampling decision, transaction pointer, and span recorder are
        inherited from the current span/transaction.
        """
        kwargs.setdefault("sampled", self.sampled)

        rv = Span(
            trace_id=self.trace_id, span_id=None, parent_span_id=self.span_id, **kwargs
        )

        if isinstance(self, Transaction):
            rv._containing_transaction = self
        else:
            rv._containing_transaction = self._containing_transaction

        rv._span_recorder = recorder = self._span_recorder
        if recorder:
            recorder.add(rv)
        return rv

    def new_span(self, **kwargs):
        # type: (**Any) -> Span
        """Deprecated: use start_child instead."""
        logger.warning("Deprecated: use Span.start_child instead of Span.new_span.")
        return self.start_child(**kwargs)

    @classmethod
    def continue_from_environ(
        cls,
        environ,  # type: typing.Mapping[str, str]
        **kwargs  # type: Any
    ):
        # type: (...) -> Transaction
        """
        Create a Transaction with the given params, then add in data pulled from
        the 'sentry-trace' header in the environ (if any) before returning the
        Transaction.

        If the 'sentry-trace' header is malformed or missing, just create and
        return a Transaction instance with the given params.
        """
        if cls is Span:
            logger.warning(
                "Deprecated: use Transaction.continue_from_environ "
                "instead of Span.continue_from_environ."
            )
        return Transaction.continue_from_headers(EnvironHeaders(environ), **kwargs)

    @classmethod
    def continue_from_headers(
        cls,
        headers,  # type: typing.Mapping[str, str]
        **kwargs  # type: Any
    ):
        # type: (...) -> Transaction
        """
        Create a Transaction with the given params, then add in data pulled from
        the 'sentry-trace' header (if any) before returning the Transaction.

        If the 'sentry-trace' header is malformed or missing, just create and
        return a Transaction instance with the given params.
        """
        if cls is Span:
            logger.warning(
                "Deprecated: use Transaction.continue_from_headers "
                "instead of Span.continue_from_headers."
            )
        transaction = Transaction.from_traceparent(
            headers.get("sentry-trace"), **kwargs
        )
        if transaction is None:
            transaction = Transaction(**kwargs)
        transaction.same_process_as_parent = False
        return transaction

    def iter_headers(self):
        # type: () -> Generator[Tuple[str, str], None, None]
        yield "sentry-trace", self.to_traceparent()

    @classmethod
    def from_traceparent(
        cls,
        traceparent,  # type: Optional[str]
        **kwargs  # type: Any
    ):
        # type: (...) -> Optional[Transaction]
        """
        Create a Transaction with the given params, then add in data pulled from
        the given 'sentry-trace' header value before returning the Transaction.

        If the header value is malformed or missing, just create and return a
        Transaction instance with the given params.
        """
        if cls is Span:
            logger.warning(
                "Deprecated: use Transaction.from_traceparent "
                "instead of Span.from_traceparent."
            )

        if not traceparent:
            return None

        if traceparent.startswith("00-") and traceparent.endswith("-00"):
            traceparent = traceparent[3:-3]

        match = _traceparent_header_format_re.match(str(traceparent))
        if match is None:
            return None

        trace_id, parent_span_id, sampled_str = match.groups()

        if trace_id is not None:
            trace_id = "{:032x}".format(int(trace_id, 16))
        if parent_span_id is not None:
            parent_span_id = "{:016x}".format(int(parent_span_id, 16))

        if sampled_str:
            parent_sampled = sampled_str != "0"  # type: Optional[bool]
        else:
            parent_sampled = None

        return Transaction(
            trace_id=trace_id,
            parent_span_id=parent_span_id,
            parent_sampled=parent_sampled,
            **kwargs
        )

    def to_traceparent(self):
        # type: () -> str
        sampled = ""
        if self.sampled is True:
            sampled = "1"
        if self.sampled is False:
            sampled = "0"
        return "%s-%s-%s" % (self.trace_id, self.span_id, sampled)

    def set_tag(self, key, value):
        # type: (str, Any) -> None
        self._tags[key] = value

    def set_data(self, key, value):
        # type: (str, Any) -> None
        self._data[key] = value

    def set_status(self, value):
        # type: (str) -> None
        self.status = value

    def set_http_status(self, http_status):
        # type: (int) -> None
        self.set_tag("http.status_code", str(http_status))

        if http_status < 400:
            self.set_status("ok")
        elif 400 <= http_status < 500:
            if http_status == 403:
                self.set_status("permission_denied")
            elif http_status == 404:
                self.set_status("not_found")
            elif http_status == 429:
                self.set_status("resource_exhausted")
            elif http_status == 413:
                self.set_status("failed_precondition")
            elif http_status == 401:
                self.set_status("unauthenticated")
            elif http_status == 409:
                self.set_status("already_exists")
            else:
                self.set_status("invalid_argument")
        elif 500 <= http_status < 600:
            if http_status == 504:
                self.set_status("deadline_exceeded")
            elif http_status == 501:
                self.set_status("unimplemented")
            elif http_status == 503:
                self.set_status("unavailable")
            else:
                self.set_status("internal_error")
        else:
            self.set_status("unknown_error")

    def is_success(self):
        # type: () -> bool
        return self.status == "ok"

    def finish(self, hub=None):
        # type: (Optional[sentry_sdk.Hub]) -> Optional[str]
        # XXX: would be type: (Optional[sentry_sdk.Hub]) -> None, but that leads
        # to incompatible return types for Span.finish and Transaction.finish.
        if self.timestamp is not None:
            # This span is already finished, ignore.
            return None

        hub = hub or self.hub or sentry_sdk.Hub.current

        try:
            duration_seconds = time.perf_counter() - self._start_timestamp_monotonic
            self.timestamp = self.start_timestamp + timedelta(seconds=duration_seconds)
        except AttributeError:
            self.timestamp = datetime.utcnow()

        _maybe_create_breadcrumbs_from_span(hub, self)
        return None

    def to_json(self):
        # type: () -> Dict[str, Any]
        rv = {
            "trace_id": self.trace_id,
            "span_id": self.span_id,
            "parent_span_id": self.parent_span_id,
            "same_process_as_parent": self.same_process_as_parent,
            "op": self.op,
            "description": self.description,
            "start_timestamp": self.start_timestamp,
            "timestamp": self.timestamp,
        }  # type: Dict[str, Any]

        if self.status:
            self._tags["status"] = self.status

        tags = self._tags
        if tags:
            rv["tags"] = tags

        data = self._data
        if data:
            rv["data"] = data

        return rv

    def get_trace_context(self):
        # type: () -> Any
        rv = {
            "trace_id": self.trace_id,
            "span_id": self.span_id,
            "parent_span_id": self.parent_span_id,
            "op": self.op,
            "description": self.description,
        }
        if self.status:
            rv["status"] = self.status

        return rv


class Transaction(Span):
    __slots__ = ("name", "parent_sampled")

    def __init__(
        self,
        name="",  # type: str
        parent_sampled=None,  # type: Optional[bool]
        **kwargs  # type: Any
    ):
        # type: (...) -> None
        # TODO: consider removing this in a future release.
        # This is for backwards compatibility with releases before Transaction
        # existed, to allow for a smoother transition.
        if not name and "transaction" in kwargs:
            logger.warning(
                "Deprecated: use Transaction(name=...) to create transactions "
                "instead of Span(transaction=...)."
            )
            name = kwargs.pop("transaction")
        Span.__init__(self, **kwargs)
        self.name = name
        self.parent_sampled = parent_sampled

    def __repr__(self):
        # type: () -> str
        return "<%s(name=%r, op=%r, trace_id=%r, span_id=%r, parent_span_id=%r, sampled=%r)>" % (
            self.__class__.__name__,
            self.name,
            self.op,
            self.trace_id,
            self.span_id,
            self.parent_span_id,
            self.sampled,
        )

    def finish(self, hub=None):
        # type: (Optional[sentry_sdk.Hub]) -> Optional[str]
        if self.timestamp is not None:
            # This transaction is already finished, ignore.
            return None

        # This is a de facto proxy for checking if sampled = False
        if self._span_recorder is None:
            logger.debug("Discarding transaction because sampled = False")
            return None

        hub = hub or self.hub or sentry_sdk.Hub.current
        client = hub.client

        if client is None:
            # We have no client and therefore nowhere to send this transaction.
            return None

        if not self.name:
            logger.warning(
                "Transaction has no name, falling back to `<unlabeled transaction>`."
            )
            self.name = "<unlabeled transaction>"

        Span.finish(self, hub)

        if not self.sampled:
            # At this point a `sampled = None` should have already been resolved
            # to a concrete decision.
            if self.sampled is None:
                logger.warning("Discarding transaction without sampling decision.")
            return None

        finished_spans = [
            span.to_json()
            for span in self._span_recorder.spans
            if span is not self and span.timestamp is not None
        ]

        return hub.capture_event(
            {
                "type": "transaction",
                "transaction": self.name,
                "contexts": {"trace": self.get_trace_context()},
                "tags": self._tags,
                "timestamp": self.timestamp,
                "start_timestamp": self.start_timestamp,
                "spans": finished_spans,
            }
        )

    def to_json(self):
        # type: () -> Dict[str, Any]
        rv = super(Transaction, self).to_json()

        rv["name"] = self.name
        rv["sampled"] = self.sampled

        return rv

    def _set_initial_sampling_decision(self, sampling_context):
        # type: (SamplingContext) -> None
        """
        Sets the transaction's sampling decision, according to the following
        precedence rules:

        1. If a sampling decision is passed to `start_transaction`
        (`start_transaction(name: "my transaction", sampled: True)`), that
        decision will be used, regardlesss of anything else

        2. If `traces_sampler` is defined, its decision will be used. It can
        choose to keep or ignore any parent sampling decision, or use the
        sampling context data to make its own decision or to choose a sample
        rate for the transaction.

        3. If `traces_sampler` is not defined, but there's a parent sampling
        decision, the parent sampling decision will be used.

        4. If `traces_sampler` is not defined and there's no parent sampling
        decision, `traces_sample_rate` will be used.
        """

        hub = self.hub or sentry_sdk.Hub.current
        client = hub.client
        options = (client and client.options) or {}
        transaction_description = "{op}transaction <{name}>".format(
            op=("<" + self.op + "> " if self.op else ""), name=self.name
        )

        # nothing to do if there's no client or if tracing is disabled
        if not client or not has_tracing_enabled(options):
            self.sampled = False
            return

        # if the user has forced a sampling decision by passing a `sampled`
        # value when starting the transaction, go with that
        if self.sampled is not None:
            return

        # we would have bailed already if neither `traces_sampler` nor
        # `traces_sample_rate` were defined, so one of these should work; prefer
        # the hook if so
        sample_rate = (
            options["traces_sampler"](sampling_context)
            if callable(options.get("traces_sampler"))
            else (
                # default inheritance behavior
                sampling_context["parent_sampled"]
                if sampling_context["parent_sampled"] is not None
                else options["traces_sample_rate"]
            )
        )

        # Since this is coming from the user (or from a function provided by the
        # user), who knows what we might get. (The only valid values are
        # booleans or numbers between 0 and 1.)
        if not _is_valid_sample_rate(sample_rate):
            logger.warning(
                "[Tracing] Discarding {transaction_description} because of invalid sample rate.".format(
                    transaction_description=transaction_description,
                )
            )
            self.sampled = False
            return

        # if the function returned 0 (or false), or if `traces_sample_rate` is
        # 0, it's a sign the transaction should be dropped
        if not sample_rate:
            logger.debug(
                "[Tracing] Discarding {transaction_description} because {reason}".format(
                    transaction_description=transaction_description,
                    reason=(
                        "traces_sampler returned 0 or False"
                        if callable(options.get("traces_sampler"))
                        else "traces_sample_rate is set to 0"
                    ),
                )
            )
            self.sampled = False
            return

        # Now we roll the dice. random.random is inclusive of 0, but not of 1,
        # so strict < is safe here. In case sample_rate is a boolean, cast it
        # to a float (True becomes 1.0 and False becomes 0.0)
        self.sampled = random.random() < float(sample_rate)

        if self.sampled:
            logger.debug(
                "[Tracing] Starting {transaction_description}".format(
                    transaction_description=transaction_description,
                )
            )
        else:
            logger.debug(
                "[Tracing] Discarding {transaction_description} because it's not included in the random sample (sampling rate = {sample_rate})".format(
                    transaction_description=transaction_description,
                    sample_rate=float(sample_rate),
                )
            )


def has_tracing_enabled(options):
    # type: (Dict[str, Any]) -> bool
    """
    Returns True if either traces_sample_rate or traces_sampler is
    defined, False otherwise.
    """

    return bool(
        options.get("traces_sample_rate") is not None
        or options.get("traces_sampler") is not None
    )


def _is_valid_sample_rate(rate):
    # type: (Any) -> bool
    """
    Checks the given sample rate to make sure it is valid type and value (a
    boolean or a number between 0 and 1, inclusive).
    """

    # both booleans and NaN are instances of Real, so a) checking for Real
    # checks for the possibility of a boolean also, and b) we have to check
    # separately for NaN
    if not isinstance(rate, Real) or math.isnan(rate):
        logger.warning(
            "[Tracing] Given sample rate is invalid. Sample rate must be a boolean or a number between 0 and 1. Got {rate} of type {type}.".format(
                rate=rate, type=type(rate)
            )
        )
        return False

    # in case rate is a boolean, it will get cast to 1 if it's True and 0 if it's False
    rate = float(rate)
    if rate < 0 or rate > 1:
        logger.warning(
            "[Tracing] Given sample rate is invalid. Sample rate must be between 0 and 1. Got {rate}.".format(
                rate=rate
            )
        )
        return False

    return True


def _format_sql(cursor, sql):
    # type: (Any, str) -> Optional[str]

    real_sql = None

    # If we're using psycopg2, it could be that we're
    # looking at a query that uses Composed objects. Use psycopg2's mogrify
    # function to format the query. We lose per-parameter trimming but gain
    # accuracy in formatting.
    try:
        if hasattr(cursor, "mogrify"):
            real_sql = cursor.mogrify(sql)
            if isinstance(real_sql, bytes):
                real_sql = real_sql.decode(cursor.connection.encoding)
    except Exception:
        real_sql = None

    return real_sql or to_string(sql)


@contextlib.contextmanager
def record_sql_queries(
    hub,  # type: sentry_sdk.Hub
    cursor,  # type: Any
    query,  # type: Any
    params_list,  # type:  Any
    paramstyle,  # type: Optional[str]
    executemany,  # type: bool
):
    # type: (...) -> Generator[Span, None, None]

    # TODO: Bring back capturing of params by default
    if hub.client and hub.client.options["_experiments"].get(
        "record_sql_params", False
    ):
        if not params_list or params_list == [None]:
            params_list = None

        if paramstyle == "pyformat":
            paramstyle = "format"
    else:
        params_list = None
        paramstyle = None

    query = _format_sql(cursor, query)

    data = {}
    if params_list is not None:
        data["db.params"] = params_list
    if paramstyle is not None:
        data["db.paramstyle"] = paramstyle
    if executemany:
        data["db.executemany"] = True

    with capture_internal_exceptions():
        hub.add_breadcrumb(message=query, category="query", data=data)

    with hub.start_span(op="db", description=query) as span:
        for k, v in data.items():
            span.set_data(k, v)
        yield span


def _maybe_create_breadcrumbs_from_span(hub, span):
    # type: (sentry_sdk.Hub, Span) -> None
    if span.op == "redis":
        hub.add_breadcrumb(
            message=span.description, type="redis", category="redis", data=span._tags
        )
    elif span.op == "http":
        hub.add_breadcrumb(type="http", category="httplib", data=span._data)
    elif span.op == "subprocess":
        hub.add_breadcrumb(
            type="subprocess",
            category="subprocess",
            message=span.description,
            data=span._data,
        )
