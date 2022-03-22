package rw.tests.fixtures;

import org.apache.commons.io.FileUtils;
import org.powermock.reflect.Whitebox;
import rw.config.Config;
import rw.config.Stage;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import org.mockito.Mockito;


public class ConfigFixture {
    Config device;

    boolean makeProd;

    public ConfigFixture(boolean makeProd) {
        this.makeProd = makeProd;
    }

    public void start() throws Exception {
        this.device = spy(new Config());
        Config.singleton = this.device;

        Path dotDir = Path.of(Files.createTempDirectory(".reloadium").toFile().getAbsolutePath());

        Mockito.lenient().doReturn(dotDir).when(this.device).getDotDir();

        if (this.makeProd) {
            this.device.stage = Stage.PROD;
        }
    }

    public void stop() throws Exception {
    }
}

