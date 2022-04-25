package rw.pkg.wheel;

import rw.consts.Const;
import rw.pkg.Architecture;
import rw.util.OsType;

import java.io.File;

public class WinWheel extends BaseWheel {
    WinWheel(String url) {
        super(url);
    }

    protected void parse() {
        super.parse();

        this.osType = OsType.Windows;

        if( this.input.contains("win32")) {
            this.architecture = Architecture.x86;
        }
        else {
            this.architecture = Architecture.x64;
        }
    }

    @Override
    public File getPackageDir() {
        return Const.get().getPackagePythonVersionDir(this.getPythonVersion(), this.getArchitecture());
    }
}
