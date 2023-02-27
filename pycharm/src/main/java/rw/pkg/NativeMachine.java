package rw.pkg;

import rw.util.Architecture;
import rw.util.OsType;

public class NativeMachine extends Machine {
    public OsType getOsType() {
        return OsType.DETECTED;
    }

    @Override
    public Architecture getArchitecture() {
        return Architecture.DETECTED;
    }
}
