import os
import sys
from typing import Tuple

IMPORT_ERROR_MSG = (
    "It seems like your platform or Python version are not supported yet.\n"
    "Windows, Linux, macOS (except M1 chip) and Python >= 3.6 are currently supported.\n"
    "Please submit a github issue if you believe Reloadium should be working on your system at\n"
    "https://github.com/reloadware/reloadium\n"
)


def colored(inp: str, color: Tuple[int, int, int]) -> str:
    ret = f"\033[38;2;{color[0]};{color[1]};{color[2]}m{inp}\x1b[0m"
    return ret


def pre_import_check() -> None:
    try:
        import reloadium.reloader
    except Exception as e:
        sys.stderr.write(colored(IMPORT_ERROR_MSG, (255, 0, 0)))
        sys.stderr.flush()

        if os.environ.get("RW_DEBUG", "False") != "False":
            raise

        sys.exit(1)
