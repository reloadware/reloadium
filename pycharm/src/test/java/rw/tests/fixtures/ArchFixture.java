package rw.tests.fixtures;

import org.powermock.reflect.Whitebox;
import rw.util.Architecture;


public class ArchFixture {
    Architecture original;
    Architecture toSet;

    public ArchFixture(Architecture toSet) {
        this.toSet = toSet;
    }

    public void start() {
        this.original = Architecture.DETECTED;
        Whitebox.setInternalState(Architecture.class, "DETECTED", this.toSet);
    }

    public void stop() {
        Whitebox.setInternalState(Architecture.class, "DETECTED", this.original);
    }
}
