package rw.tests.fixtures;

import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import rw.config.Config;
import rw.config.Stage;
import rw.util.OsType;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.spy;


public class WinFixture {
    OsType original;

    public WinFixture() {
    }

    public void start() {
        this.original = OsType.DETECTED;
        Whitebox.setInternalState(OsType.class, "DETECTED", OsType.Windows);
    }

    public void stop() {
        Whitebox.setInternalState(OsType.class, "DETECTED", this.original);
    }
}

