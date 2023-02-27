package rw.pkg.wheel;

import rw.consts.Const;
import rw.util.Architecture;
import rw.util.OsType;

import java.io.File;

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
