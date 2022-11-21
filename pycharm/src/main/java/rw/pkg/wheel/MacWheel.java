package rw.pkg.wheel;

import rw.util.Architecture;
import rw.util.OsType;

public class MacWheel extends BaseWheel {
    MacWheel(String url) {
        super(url);
    }

    @Override
    protected void parse() {
        super.parse();

        this.osType = OsType.MacOS;

        if( this.input.contains("arm64")) {
            this.architecture = Architecture.ARM64;
        }
        else {
            this.architecture = Architecture.X64;
        }
    }
}
