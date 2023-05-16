package rw.tests.fixtures;

import org.powermock.reflect.Whitebox;
import rw.util.OsType;


public class OsFixture {
    OsType original;
    OsType toSet;

    public OsFixture(OsType osType) {
        this.toSet = osType;
    }

    public void start() {
        this.original = OsType.DETECTED;
        Whitebox.setInternalState(OsType.class, "DETECTED", this.toSet);
    }

    public void stop() {
        Whitebox.setInternalState(OsType.class, "DETECTED", this.original);
    }
}

