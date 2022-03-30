package rw.pkg.wheel;

import rw.config.Config;

import java.io.File;

public class TarWheel extends BaseWheel {
    TarWheel(String url) {
        super(url);
    }

    protected void parse() {
    }

    @Override
    public File getPackageDir() {
        return Config.get().getPackagePythonVersionDir(this.getPythonVersion(), this.getArchitecture());
    }
}
