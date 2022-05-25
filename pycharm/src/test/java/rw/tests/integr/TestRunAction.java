package rw.tests.integr;

import com.intellij.execution.RunManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.jetbrains.python.run.PythonRunConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import rw.action.RunWithReloadium;
import rw.pkg.Architecture;
import rw.tests.BaseMockedTestCase;
import rw.tests.fixtures.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.intellij.testFramework.TestActionEvent;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestRunAction extends BaseMockedTestCase {
    CakeshopFixture cakeshop;
    AnAction action;
    SdkFixture oldSdkFixture;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        PackageFixture packageFixture = new PackageFixture("0.7.12");
        this.cakeshop = new CakeshopFixture(this.getProject());
        this.cakeshop.start();

        this.oldSdkFixture = new SdkFixture(this.cakeshop.getRoot().toFile(), "3.2");
        this.oldSdkFixture.start();

        this.action = ActionManager.getInstance().getAction(RunWithReloadium.ID);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.cakeshop.stop();
        this.oldSdkFixture.stop();

        super.tearDown();
    }

    @Test
    public void testUpdateNoConf() {
        this.cakeshop.stop();

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);

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
    public void testNonSupportedConf() {
        RunManager.getInstance(this.getProject()).setSelectedConfiguration(this.cakeshop.getNonPySettings());

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);

        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isFalse();
    }

    @Test
    public void testNonSupportedSdkVersion() {
        this.cakeshop.getRunConf().setSdkHome(this.oldSdkFixture.getSdkHome().toString());

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);

        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();
    }

    @Test
    public void testBadSdk() {
        this.cakeshop.getRunConf().setSdkHome("BadDir/python");

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);

        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isFalse();
    }

    @Test
    public void testActionPerformedNonSupportedSdkVersion() {
        this.cakeshop.getRunConf().setSdkHome(this.oldSdkFixture.getSdkHome().toString());

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);

        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();

        this.action.actionPerformed(event);
        String pythonpath = this.cakeshop.getRunConf().getEnvs().get("PYTHONPATH");
        assertThat(pythonpath.endsWith("3.6")).isTrue();
    }

    @Test
    public void testActionPerformed() {
        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();
        this.action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        assertThat(runConf.getScriptName()).isEqualTo("main.py");
        assertThat(runConf.isModuleMode()).isFalse();

        String pythonpath = runConf.getEnvs().get("PYTHONPATH");
        assertThat(pythonpath.endsWith("3.9")).isTrue();

        assertThat(runConf.getInterpreterOptions()).isEqualTo("-m reloadium run");
        verify(this.dialogFactoryFixture.dialogFactory, times(1)).showFirstRunDialog(this.getProject());
    }

    @Test
    public void testActionPerformedWin32() throws Exception {
        WinSdkFixture winSdkFixture = new WinSdkFixture(this.cakeshop.getRoot().toFile(), "3.9", Architecture.x86);
        winSdkFixture.start();

        this.cakeshop.getRunConf().setSdkHome(winSdkFixture.getSdkHome().toString());

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();
        this.action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        assertThat(runConf.getScriptName()).isEqualTo("main.py");
        assertThat(runConf.isModuleMode()).isFalse();

        String pythonpath = this.cakeshop.getRunConf().getEnvs().get("PYTHONPATH");
        assertThat(pythonpath.endsWith("3.9-32")).isTrue();

        assertThat(runConf.getInterpreterOptions()).isEqualTo("-m reloadium run");

        winSdkFixture.stop();
    }

    @Test
    public void testModuleActionPerformed() {
        this.cakeshop.getRunConf().setModuleMode(true);
        this.cakeshop.getRunConf().setScriptName("main");

        AnAction action = ActionManager.getInstance().getAction(RunWithReloadium.ID);

        AnActionEvent event = new TestActionEvent();
        action.update(event);
        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();
        action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        assertThat(runConf.getScriptName()).isEqualTo("main");
        assertThat(runConf.isModuleMode()).isTrue();
        assertThat(runConf.getEnvs().get("PYTHONPATH").isBlank()).isFalse();
        assertThat(runConf.getInterpreterOptions()).isEqualTo("-m reloadium run");
    }
}
