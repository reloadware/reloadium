package rw.pkg.wheel;

import rw.config.Config;

import java.nio.file.Path;

public class NoneAnyWheel extends BaseWheel {
    NoneAnyWheel(String url) {
        super(url);
    }

    protected void parse() {
    }

    @Override
    public Path getPackageDir() {
        return Config.get().getPackagePythonVersionDir(this.getPythonVersion(), this.getArchitecture());
    }
}
