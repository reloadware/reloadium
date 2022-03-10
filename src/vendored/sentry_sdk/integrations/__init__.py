"""This package"""
from __future__ import absolute_import

from threading import Lock

from reloadium.vendored.sentry_sdk._compat import iteritems
from reloadium.vendored.sentry_sdk.utils import logger

from reloadium.vendored.sentry_sdk._types import MYPY

if MYPY:
    from typing import Callable
    from typing import Dict
    from typing import Iterator
    from typing import List
    from typing import Set
    from typing import Tuple
    from typing import Type


_installer_lock = Lock()
_installed_integrations = set()  # type: Set[str]


def _generate_default_integrations_iterator(integrations, auto_enabling_integrations):
    # type: (Tuple[str, ...], Tuple[str, ...]) -> Callable[[bool], Iterator[Type[Integration]]]

    def iter_default_integrations(with_auto_enabling_integrations):
        # type: (bool) -> Iterator[Type[Integration]]
        """Returns an iterator of the default integration classes:"""
        from importlib import import_module

        if with_auto_enabling_integrations:
            all_import_strings = integrations + auto_enabling_integrations
        else:
            all_import_strings = integrations

        for import_string in all_import_strings:
            try:
                module, cls = import_string.rsplit(".", 1)
                yield getattr(import_module(module), cls)
            except (DidNotEnable, SyntaxError) as e:
                logger.debug(
                    "Did not import default integration %s: %s", import_string, e
                )

    if isinstance(iter_default_integrations.__doc__, str):
        for import_string in integrations:
            iter_default_integrations.__doc__ += "\n- `{}`".format(import_string)

    return iter_default_integrations


_AUTO_ENABLING_INTEGRATIONS = (
)


iter_default_integrations = _generate_default_integrations_iterator(
    integrations=(
        # stdlib/base runtime integrations
        "reloadium.vendored.sentry_sdk.integrations.logging.LoggingIntegration",
        "reloadium.vendored.sentry_sdk.integrations.stdlib.StdlibIntegration",
        "reloadium.vendored.sentry_sdk.integrations.excepthook.ExcepthookIntegration",
        "reloadium.vendored.sentry_sdk.integrations.atexit.AtexitIntegration",
        "reloadium.vendored.sentry_sdk.integrations.modules.ModulesIntegration",
        "reloadium.vendored.sentry_sdk.integrations.argv.ArgvIntegration",
        "reloadium.vendored.sentry_sdk.integrations.threading.ThreadingIntegration",
    ),
    auto_enabling_integrations=_AUTO_ENABLING_INTEGRATIONS,
)

del _generate_default_integrations_iterator


def setup_integrations(
    integrations, with_defaults=True, with_auto_enabling_integrations=False
):
    # type: (List[Integration], bool, bool) -> Dict[str, Integration]
    """Given a list of integration instances this installs them all.  When
    `with_defaults` is set to `True` then all default integrations are added
    unless they were already provided before.
    """
    integrations = dict(
        (integration.identifier, integration) for integration in integrations or ()
    )

    logger.debug("Setting up integrations (with default = %s)", with_defaults)

    # Integrations that are not explicitly set up by the user.
    used_as_default_integration = set()

    if with_defaults:
        for integration_cls in iter_default_integrations(
            with_auto_enabling_integrations
        ):
            if integration_cls.identifier not in integrations:
                instance = integration_cls()
                integrations[instance.identifier] = instance
                used_as_default_integration.add(instance.identifier)

    for identifier, integration in iteritems(integrations):
        with _installer_lock:
            if identifier not in _installed_integrations:
                logger.debug(
                    "Setting up previously not enabled integration %s", identifier
                )
                try:
                    type(integration).setup_once()
                except NotImplementedError:
                    if getattr(integration, "install", None) is not None:
                        logger.warning(
                            "Integration %s: The install method is "
                            "deprecated. Use `setup_once`.",
                            identifier,
                        )
                        integration.install()
                    else:
                        raise
                except DidNotEnable as e:
                    if identifier not in used_as_default_integration:
                        raise

                    logger.debug(
                        "Did not enable default integration %s: %s", identifier, e
                    )

                _installed_integrations.add(identifier)

    for identifier in integrations:
        logger.debug("Enabling integration %s", identifier)

    return integrations


class DidNotEnable(Exception):
    """
    The integration could not be enabled due to a trivial user error like
    `flask` not being installed for the `FlaskIntegration`.

    This exception is silently swallowed for default integrations, but reraised
    for explicitly enabled integrations.
    """


class Integration(object):
    """Baseclass for all integrations.

    To accept options for an integration, implement your own constructor that
    saves those options on `self`.
    """

    install = None
    """Legacy method, do not implement."""

    identifier = None  # type: str
    """String unique ID of integration type"""

    @staticmethod
    def setup_once():
        # type: () -> None
        """
        Initialize the integration.

        This function is only called once, ever. Configuration is not available
        at this point, so the only thing to do here is to hook into exception
        handlers, and perhaps do monkeypatches.

        Inside those hooks `Integration.current` can be used to access the
        instance again.
        """
        raise NotImplementedError()
