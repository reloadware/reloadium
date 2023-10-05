package rw.tests.fixtures;

import com.intellij.openapi.util.io.FileUtil;
import rw.config.ConfigManager;
import rw.depot.Depot;
import rw.pkg.FileSystem;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class SandboxFixture {
    File sandbox;

    public SandboxFixture() {
    }

    public void setUp() {
        try {
            this.sandbox = FileUtil.createTempDirectory( new File("/tmp/"), "sandbox", "", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void tearDown() {
        this.sandbox.delete();
    }

    public File getSandbox() {
        return this.sandbox;
    }
}

