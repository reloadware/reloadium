import sys
import platform
from pathlib import Path


__RELOADIUM__ = True


def set_path():
    package_dir = Path(f"package_local") / f"{sys.version_info[0]}.{sys.version_info[1]}"

    if platform.machine().startswith("arm") or platform.machine().startswith("aarch"):
        package_dir = f"{package_dir}_arm64"

    remote_package_path = Path("/root/.reloadium") / package_dir
    local_package_path = Path.home() / ".reloadium" / package_dir

    sys.path.append(str(remote_package_path))
    sys.path.append(str(local_package_path))


if __name__ == "__main__":
    set_path()
    
    from reloadium.corium import start
    start(sys.argv)