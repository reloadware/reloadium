package rw.tests.fixtures;

import rw.util.Architecture;
import rw.util.OsType;


public class WinFixture {
    OsFixture osFixture;
    ArchFixture archFixture;

    public WinFixture() {
        this.osFixture = new OsFixture(OsType.Windows);
        this.archFixture = new ArchFixture(Architecture.X64);
    }

    public void start() {
        this.osFixture.start();
        this.archFixture.start();
    }

    public void stop() {
        this.osFixture.stop();
        this.archFixture.stop();
    }
}
