package rw.pkg.wheel;

import rw.pkg.Machine;
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
        this.architecture = Architecture.X64;
    }

    public boolean accepts(Machine machine) {
        return this.osType == machine.getOsType();
    }
}
