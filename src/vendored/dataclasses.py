import sys
from typing import TYPE_CHECKING
from importlib import import_module

dataclasses_version = sys.version_info.minor
globals().update(import_module(f'reloadium.vendored.dataclasses3{dataclasses_version}').__dict__)


if TYPE_CHECKING:
    from reloadium.vendored.dataclasses36 import *
