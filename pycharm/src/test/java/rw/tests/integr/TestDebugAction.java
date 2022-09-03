package rw.tests.integr;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.testFramework.TestActionEvent;
import com.jetbrains.python.run.PythonRunConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import rw.action.DebugWithReloadium;
import rw.tests.BaseMockedTestCase;
import rw.tests.fixtures.CakeshopFixture;
import rw.tests.fixtures.DialogFactoryFixture;
import rw.tests.fixtures.PackageFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class TestDebugAction extends BaseMockedTestCase {
    CakeshopFixture cakeshop;
    DialogFactoryFixture dialogFactoryFixture;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        PackageFixture packageFixture = new PackageFixture("0.7.12");
        this.cakeshop = new CakeshopFixture(this.getProject());
        this.cakeshop.start();

        this.dialogFactoryFixture = new DialogFactoryFixture(this.getProject());
        this.dialogFactoryFixture.start();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.cakeshop.stop();
        this.dialogFactoryFixture.stop();

        super.tearDown();
    }

    @Test
    public void testUpdateNoConf() {
        this.cakeshop.stop();
        AnAction action = ActionManager.getInstance().getAction(DebugWithReloadium.ID);

        AnActionEvent event = new TestActionEvent();
        action.update(event);

        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isFalse();
    }

    @Test
    public void testUpdate() {
        AnAction action = ActionManager.getInstance().getAction(DebugWithReloadium.ID);

        AnActionEvent event = new TestActionEvent();
        action.update(event);

        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();
    }

    @Test
    public void testActionPerformed() {
        AnAction action = ActionManager.getInstance().getAction(DebugWithReloadium.ID);

        AnActionEvent event = new TestActionEvent();
        action.update(event);
        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();
        action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        assertThat(runConf.getScriptName()).isEqualTo("main.py");
        assertThat(runConf.isModuleMode()).isFalse();
        assertThat(runConf.getEnvs().get("PYTHONPATH").isBlank()).isFalse();
        assertThat(runConf.getInterpreterOptions()).isEqualTo("-m reloadium pydev_proxy");

        verify(this.dialogFactoryFixture.dialogFactory, times(1)).showFirstDebugDialog(this.getProject());
    }
}
