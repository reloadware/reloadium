package rw.tests.fixtures;

import org.mockito.Mockito;
import rw.pkg.NativeFileSystem;

import java.io.File;
import java.nio.file.Files;

import static org.mockito.Mockito.spy;


public class NativeFileSystemFixture {
    public NativeFileSystemFixture() {

    }

    public void start() throws Exception {
        File homeDir = new File(Files.createTempDirectory("home").toFile().getAbsolutePath());
        NativeFileSystem fs = spy(new NativeFileSystem());
        NativeFileSystem.singleton = fs;

        Mockito.lenient().doReturn(homeDir).when(fs).getHome();
    }

    public void stop() throws Exception {
    }
}
