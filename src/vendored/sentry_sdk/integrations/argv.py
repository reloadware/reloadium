from __future__ import absolute_import

import sys

from reloadium.vendored.sentry_sdk.hub import Hub
from reloadium.vendored.sentry_sdk.integrations import Integration
from reloadium.vendored.sentry_sdk.scope import add_global_event_processor

from reloadium.vendored.sentry_sdk._types import MYPY

if MYPY:
    from typing import Optional

    from reloadium.vendored.sentry_sdk._types import Event, Hint


class ArgvIntegration(Integration):
    identifier = "argv"

    @staticmethod
    def setup_once():
        # type: () -> None
        @add_global_event_processor
        def processor(event, hint):
            # type: (Event, Optional[Hint]) -> Optional[Event]
            if Hub.current.get_integration(ArgvIntegration) is not None:
                extra = event.setdefault("extra", {})
                # If some event processor decided to set extra to e.g. an
                # `int`, don't crash. Not here.
                if isinstance(extra, dict):
                    extra["sys.argv"] = sys.argv

            return event