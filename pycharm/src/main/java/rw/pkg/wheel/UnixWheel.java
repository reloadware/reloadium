package rw.pkg.wheel;

import rw.util.Architecture;
import rw.util.OsType;

public class UnixWheel extends BaseWheel {
    UnixWheel(String url) {
        super(url);
    }

    @Override
    protected void parse() {
        super.parse();

        this.osType = OsType.Linux;
        this.architecture = Architecture.X64;
    }
}
