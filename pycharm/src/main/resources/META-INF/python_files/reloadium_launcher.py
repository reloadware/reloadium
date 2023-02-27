import sys
import os
from pathlib import Path


__RELOADIUM__ = True


if __name__ == "__main__":
    package_dir = Path(f"package_local") / f"{sys.version_info[0]}.{sys.version_info[1]}"
    remote_package_path = Path("/root/.reloadium") / package_dir
    local_package_path = Path.home() / ".reloadium" / package_dir

    sys.path.insert(0, str(remote_package_path))
    sys.path.insert(0, str(local_package_path))

    from reloadium.corium import start
    start(sys.argv)