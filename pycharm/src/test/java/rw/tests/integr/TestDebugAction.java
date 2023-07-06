package rw.tests.integr;

import com.intellij.execution.RunManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.TestActionEvent;
import com.jetbrains.python.run.PythonRunConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import rw.action.DebugWithReloadium;
import rw.action.WithReloaderBase;
import rw.dialogs.DialogsState;
import rw.tests.BaseTestCase;
import rw.tests.fixtures.CakeshopFixture;
import rw.tests.fixtures.DialogFactoryFixture;
import rw.tests.fixtures.PackageFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class TestDebugAction extends BaseTestCase {
    CakeshopFixture cakeshop;

    WithReloaderBase action;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        this.cakeshop = new CakeshopFixture(this.f);
        this.cakeshop.setUp();

        this.action = this.getWithReloaderBaseAction(DebugWithReloadium.ID);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.cakeshop.tearDown();
        super.tearDown();
    }

    @Test
    public void testUpdateNoConf() throws Exception {
        RunManager runManager = RunManager.getInstance(this.getProject());
        runManager.setSelectedConfiguration(null);

        AnActionEvent event = new TestActionEvent();
        EdtTestUtil.runInEdtAndWait(() -> {
                    this.action.update(event);
                });

        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isFalse();
    }

    @Test
    public void testUpdate() {
        AnActionEvent event = new TestActionEvent();
        this.action.update(event);

        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();
    }

    @Test
    public void testActionPerformedSourceRunConfNotChanged() {
        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();
        this.action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        assertThat(runConf.getScriptName()).isEqualTo("cakeshop.py");
        assertThat(runConf.isModuleMode()).isFalse();
        assertThat(runConf.getEnvs().get("PYTHONPATH")).isNull();
        assertThat(runConf.getInterpreterOptions()).isEqualTo("");
    }

    @Test
    public void testFirstDebugDialogShown() {
        DialogsState.get().loadState(new DialogsState());

        Mockito.reset(this.dialogFactoryFixture.dialogFactory);
        verify(this.dialogFactoryFixture.dialogFactory, times(0)).showFirstDebugDialog(this.getProject());

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();
        this.action.actionPerformed(event);

        verify(this.dialogFactoryFixture.dialogFactory, times(1)).showFirstDebugDialog(this.getProject());
    }
}
