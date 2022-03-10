# flake8: noqa E402, F401
from reloadium.__utils__ import pre_import_check

pre_import_check()

from reloadium.reloader.config import BaseConfig
from reloadium.reloader.public import *
from reloadium.reloader import shell_start

__author__ = "Damian Krystkiewicz"
__company__ = "Reloadware"
__copyright__ = "Copyright (C) 2022 Reloadware"  # RwRender: __copyright__ = "Copyright (C) {{ ctx.year }} Reloadware"
__stage__ = "local"  # RwRender: __stage__ = "{{ ctx.stage }}"
__license__ = "EULA"


def start():
    import sys
    shell_start(sys.argv)
