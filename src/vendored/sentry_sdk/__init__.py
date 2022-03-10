from reloadium.vendored.sentry_sdk.hub import Hub, init
from reloadium.vendored.sentry_sdk.scope import Scope
from reloadium.vendored.sentry_sdk.transport import Transport, HttpTransport
from reloadium.vendored.sentry_sdk.client import Client

from reloadium.vendored.sentry_sdk.api import *  # noqa

from reloadium.vendored.sentry_sdk.consts import VERSION  # noqa

__all__ = [  # noqa
    "Hub",
    "Scope",
    "Client",
    "Transport",
    "HttpTransport",
    "init",
    "integrations",
    # from reloadium.vendored.sentry_sdk.api
    "capture_event",
    "capture_message",
    "capture_exception",
    "add_breadcrumb",
    "configure_scope",
    "push_scope",
    "flush",
    "last_event_id",
    "start_span",
    "start_transaction",
    "set_tag",
    "set_context",
    "set_extra",
    "set_user",
    "set_level",
]

# Initialize the debug support after everything is loaded
from reloadium.vendored.sentry_sdk.debug import init_debug_support

init_debug_support()
del init_debug_support
