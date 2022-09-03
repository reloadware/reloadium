package rw.tests.integr;

import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.actionSystem.*;
import com.intellij.testFramework.TestActionEvent;
import com.jetbrains.python.run.PythonRunConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import rw.action.DebugWithReloadium;
import rw.action.RerunDebugWithReloadium;
import rw.consts.DataKeys;
import rw.tests.BaseMockedTestCase;
import rw.tests.fixtures.CakeshopFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class TestRerunDebugAction extends BaseMockedTestCase {
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

    private AnActionEvent getEventWithConf() {
        ExecutionEnvironment executionEnvironment = spy(ExecutionEnvironment.class);
        lenient().doReturn(this.cakeshop.getSettings()).when(executionEnvironment).getRunnerAndConfigurationSettings();

        AnActionEvent event = spy(TestActionEvent.class);
        lenient().doReturn(executionEnvironment).when(event).getData(DataKeys.EXECUTION_ENVIRONMENT);

        return event;
    }

    @Test
    public void testUpdate() {
        AnAction action = ActionManager.getInstance().getAction(DebugWithReloadium.ID);

        AnActionEvent event = getEventWithConf();
        action.update(event);

        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();
    }

    @Test
    public void testNoExecutionEnv() {
        AnAction action = ActionManager.getInstance().getAction(RerunDebugWithReloadium.ID);

        AnActionEvent event = new TestActionEvent();
        action.update(event);
        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isFalse();
        action.actionPerformed(event);
    }

    @Test
    public void testPerform() {
        AnAction action = ActionManager.getInstance().getAction(RerunDebugWithReloadium.ID);

        AnActionEvent event = getEventWithConf();

        action.update(event);
        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();
        action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        assertThat(runConf.getScriptName()).isEqualTo("main.py");
        assertThat(runConf.isModuleMode()).isFalse();
        assertThat(runConf.getEnvs().get("PYTHONPATH").isBlank()).isFalse();
        assertThat(runConf.getInterpreterOptions()).isEqualTo("-m reloadium pydev_proxy");
    }
}
