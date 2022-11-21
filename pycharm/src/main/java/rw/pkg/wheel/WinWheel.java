package rw.pkg.wheel;

import rw.util.Architecture;
import rw.util.OsType;

public class WinWheel extends BaseWheel {
    WinWheel(String url) {
        super(url);
    }

    protected void parse() {
        super.parse();

        this.osType = OsType.Windows;
        this.architecture = Architecture.X64;
    }
}
