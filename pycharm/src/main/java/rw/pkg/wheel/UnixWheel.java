package rw.pkg.wheel;

import rw.config.Config;
import rw.pkg.Architecture;
import rw.util.OsType;

import java.nio.file.Path;

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
    public Path getPackageDir() {
        return Config.get().getPackagePythonVersionDir(this.getPythonVersion());
    }
}
