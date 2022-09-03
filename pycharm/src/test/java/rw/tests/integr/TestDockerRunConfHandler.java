package rw.tests.integr;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.testFramework.TestActionEvent;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.jetbrains.python.run.PythonRunConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedStatic;
import rw.action.DebugWithReloadium;
import rw.action.RunType;
import rw.handler.runConf.DockerRunConfHandler;
import rw.handler.runConf.RemoteRunConfHandler;
import rw.tests.fixtures.CakeshopFixture;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TestDockerRunConfHandler extends BasePlatformTestCase {
    CakeshopFixture cakeshop;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        this.cakeshop = new CakeshopFixture(this.getProject());
        this.cakeshop.start();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.cakeshop.stop();

        super.tearDown();
    }

    @Test
    public void testBasic() {
        DockerRunConfHandler remoteRunConfHandler = new DockerRunConfHandler(this.cakeshop.getRunConf());
        remoteRunConfHandler.beforeRun(RunType.RUN);

        assertThat(this.cakeshop.getRunConf().getEnvs().get("RW_DOCKER")).isEqualTo("True");
    }
}
