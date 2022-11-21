package rw.tests.fixtures;

import rw.util.Architecture;
import rw.util.OsType;


public class M1Fixture {
    OsFixture osFixture;
    ArchFixture archFixture;

    public M1Fixture() {
        this.osFixture = new OsFixture(OsType.MacOS);
        this.archFixture = new ArchFixture(Architecture.ARM64);
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
