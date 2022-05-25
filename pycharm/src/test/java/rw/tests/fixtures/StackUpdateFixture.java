package rw.tests.fixtures;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.jetbrains.python.sdk.PythonSdkType;
import org.apache.commons.io.FileUtils;
import rw.handler.runConf.PythonRunConfHandler;
import rw.handler.runConf.RunConfHandlerFactory;
import rw.session.Session;
import rw.session.StackUpdate;
import rw.util.OsType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;


public class StackUpdateFixture {
    String payload;
    Session session;
    File framePath;

    public StackUpdateFixture(Session session, File framePath) throws IOException {
        this.session = session;
        this.framePath = framePath;
        this.payload = Files.readString(Path.of(this.getClass().getClassLoader().getResource("StackUpdate.json").getFile()));
        this.payload = String.format(this.payload, StackUpdate.VERSION, this.framePath);
    }

    public StackUpdate eventFactory() throws Exception {
        return (StackUpdate) session.eventFactory(this.payload);
    }
}