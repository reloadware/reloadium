package rw.pkg.wheel;

import rw.pkg.Machine;
import rw.util.Architecture;
import rw.util.OsType;

public class M1Wheel extends BaseWheel {
    M1Wheel(String url) {
        super(url);
    }

    @Override
    protected void parse() {
        super.parse();

        this.osType = OsType.MacOS;
        this.architecture = Architecture.ARM64;
    }

    public boolean accepts(Machine machine) {
        return this.osType == machine.getOsType() && machine.getArchitecture() == this.architecture;
    }

    public String getDstDirName() {
        return super.getDstDirName() + "_arm64";
    }
}
