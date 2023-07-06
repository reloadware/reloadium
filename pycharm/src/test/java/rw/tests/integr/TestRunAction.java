package rw.tests.integr;

import com.intellij.execution.RunManager;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.openapi.actionSystem.*;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.MapDataContext;
import com.intellij.testFramework.TestActionEvent;
import com.jetbrains.python.run.PythonRunConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.mockito.Mockito;
import rw.action.RunWithReloadium;
import rw.action.RunWithReloadiumRunContext;
import rw.dialogs.DialogsState;
import rw.tests.BaseTestCase;
import rw.tests.fixtures.CakeshopFixture;
import rw.tests.fixtures.PackageFixture;
import rw.tests.fixtures.SdkFixture;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class TestRunAction extends BaseTestCase {
    CakeshopFixture cakeshop;
    AnAction action;
    SdkFixture oldSdkFixture;
    PackageFixture packageFixture;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        this.packageFixture = new PackageFixture(this.packageManager, "0.7.12");
        this.cakeshop = new CakeshopFixture(this.f);
        this.cakeshop.setUp();

        this.oldSdkFixture = new SdkFixture("3.2");
        this.oldSdkFixture.start();

        this.action = this.getWithReloaderBaseAction(RunWithReloadium.ID);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.cakeshop.tearDown();
        this.oldSdkFixture.stop();

        super.tearDown();
    }

    @Test
    public void testUpdateNoConf() throws Exception {
        this.cakeshop.tearDown();

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);

        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isFalse();
    }

    @Test
    public void testRunContext() {
        this.f.configureByText("cakeshop.py", "content = True");

        AnActionEvent event = new TestActionEvent();
        RunManager runManager = RunManager.getInstance(this.getProject());
        runManager.removeConfiguration(this.cakeshop.getSettings());

        EdtTestUtil.runInEdtAndWait(() -> this.f.openFileInEditor(this.f.getFile().getVirtualFile()));

        MapDataContext dataContext = new MapDataContext();
        dataContext.put(CommonDataKeys.PROJECT, this.getProject());
        dataContext.put(CommonDataKeys.EDITOR, this.f.getEditor());
        dataContext.put(CommonDataKeys.PSI_FILE, this.f.getFile());
        AnActionEvent eventWithContext = event.withDataContext(dataContext);

        AnAction action = ActionManager.getInstance().getAction(RunWithReloadiumRunContext.ID);

        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();

        EdtTestUtil.runInEdtAndWait(() -> action.actionPerformed(eventWithContext));

        assertThat(runManager.getSelectedConfiguration().getName()).isEqualTo("cakeshop");
    }

    @Test
    public void testUpdate() {
        AnActionEvent event = new TestActionEvent();
        this.action.update(event);

        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();
    }

    @Test
    public void testNonSupportedConf() {
        RunManager.getInstance(this.getProject()).setSelectedConfiguration(this.cakeshop.getNonPySettings());

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);

        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isFalse();
    }

    @Test
    public void testNonSupportedSdkVersion() {
        this.cakeshop.getRunConf().setSdkHome(this.oldSdkFixture.getSdk().getHomePath());

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);

        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();
    }

    @Test
    public void testActionPerformedNonSupportedSdkVersion() {
        this.cakeshop.getRunConf().setSdkHome(this.oldSdkFixture.getSdk().getHomePath());

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);

        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();

        this.action.actionPerformed(event);
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

        assertThat(runConf.getInterpreterOptions()).isEqualTo("");
    }

    @Test
    public void testFirstRunDialogShown() {
        DialogsState.get().loadState(new DialogsState());

        Mockito.reset(this.dialogFactoryFixture.dialogFactory);
        verify(this.dialogFactoryFixture.dialogFactory, times(0)).showFirstRunDialog(this.getProject());

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();
        this.action.actionPerformed(event);

        verify(this.dialogFactoryFixture.dialogFactory, times(1)).showFirstRunDialog(this.getProject());
    }
}
