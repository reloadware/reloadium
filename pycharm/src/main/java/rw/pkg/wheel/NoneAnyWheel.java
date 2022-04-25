package rw.pkg.wheel;

import rw.consts.Const;

import java.io.File;

public class NoneAnyWheel extends BaseWheel {
    NoneAnyWheel(String url) {
        super(url);
    }

    protected void parse() {
    }

    @Override
    public File getPackageDir() {
        return Const.get().getPackagePythonVersionDir(this.getPythonVersion(), this.getArchitecture());
    }
}
