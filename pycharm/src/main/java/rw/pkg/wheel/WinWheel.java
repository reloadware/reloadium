package rw.pkg.wheel;

import rw.config.Config;
import rw.pkg.Architecture;
import rw.util.OsType;

import java.nio.file.Path;

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
    public Path getPackageDir() {
        return Config.get().getPackagePythonVersionDir(this.getPythonVersion(), this.getArchitecture());
    }
}
