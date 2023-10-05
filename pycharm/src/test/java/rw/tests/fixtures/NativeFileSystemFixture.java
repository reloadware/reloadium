package rw.tests.fixtures;

import org.mockito.Mockito;
import rw.pkg.NativeFileSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.mockito.Mockito.spy;


public class NativeFileSystemFixture {
    File home;

    public NativeFileSystemFixture() {
        try {
            this.home = new File(Files.createTempDirectory("home").toFile().getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUp() throws Exception {
        NativeFileSystem fs = spy(new NativeFileSystem());
        NativeFileSystem.singleton = fs;

        Mockito.lenient().doReturn(this.home).when(fs).getHome();
    }

    public void tearDown() throws Exception {
    }
}
