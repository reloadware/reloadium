package rw.pkg.wheel;

import rw.consts.Const;
import rw.pkg.Architecture;
import rw.util.OsType;

import java.io.File;

public class UnixWheel extends BaseWheel {
    UnixWheel(String url) {
        super(url);
    }

    @Override
    protected void parse() {
        super.parse();

        if (this.input.contains("linux")) {
            this.osType = OsType.Linux;
        } else if (this.input.contains("macosx")) {
            this.osType = OsType.MacOS;
        } else {
            throw new RuntimeException("Unknown Os type");
        }

        this.architecture = Architecture.x86_64;
    }

    @Override
    public File getPackageDir() {
        return Const.get().getPackagePythonVersionDir(this.getPythonVersion());
    }
}
