import sys
import os
from typing import Tuple

IMPORT_ERROR_MSG = ("It seems like your platform or Python version are not supported yet.\n"
                    "Please submit a github issue to let us know at https://github.com/reloadware/reloadium")


def is_docker() -> bool:
    path = '/proc/self/cgroup'
    return (
            os.path.exists('/.dockerenv') or
            os.path.isfile(path) and any('docker' in line for line in open(path))
    )


class OperatingSystem:
    WINDOWS = "Windows"
    LINUX = "Linux"
    MACOS = "MacOs"
    BSD = "BSD"
    UNKNOWN = "Unknown"


def colored(inp: str, color: Tuple[int, int, int]) -> str:
    ret = f"\033[38;2;{color[0]};{color[1]};{color[2]}m{inp}\x1b[0m"
    return ret


def get_os_type() -> str:
    if sys.platform.startswith("win"):
        return OperatingSystem.WINDOWS
    elif sys.platform.startswith("darwin"):
        return OperatingSystem.MACOS
    elif sys.platform.startswith("linux"):
        return OperatingSystem.LINUX
    elif sys.platform.startswith(("dragonfly", "freebsd", "netbsd", "openbsd", "bsd")):
        return OperatingSystem.BSD
    else:
        return OperatingSystem.UNKNOWN


def report_import_fail(e: Exception) -> None:
    import platform
    from urllib import request, parse
    from reloadium.__version__ import version

    architecture = " ".join(platform.architecture())
    python_version = f"{sys.version_info.major}.{sys.version_info.minor}.{sys.version_info.micro}"
    payload = {
        "reason": repr(e),
        "is_docker": is_docker(),
        "operating_system_type": get_os_type(),
        "architecture": architecture,
        "python_version": python_version,
        "version": platform.version(),
        "os_release": platform.release(),
        "release": version
    }
    data = parse.urlencode(payload).encode()
    depot_api_url = "http://depot.reloadware.local/api/"  # RwRender: depot_api_url = "{{ ctx.depot.api_url }}"
    url = parse.urljoin(depot_api_url, "misc/package_import_fail/")
    req = request.Request(url, data=data)
    request.urlopen(req)


def pre_import_check() -> None:
    try:
        import reloadium.reloader
    except Exception as e:
        sys.stderr.write(colored(IMPORT_ERROR_MSG, (255, 0, 0)))
        sys.stderr.flush()

        try:
            report_import_fail(e)
        except:
            pass

        sys.exit(1)
