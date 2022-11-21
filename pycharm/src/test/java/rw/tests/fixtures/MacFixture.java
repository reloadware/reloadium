package rw.tests.fixtures;

import rw.util.Architecture;
import rw.util.OsType;


public class MacFixture {
    OsFixture osFixture;
    ArchFixture archFixture;

    public MacFixture() {
        this.osFixture = new OsFixture(OsType.MacOS);
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
