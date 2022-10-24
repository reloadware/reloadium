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
import rw.action.RunWithReloadium;
import rw.handler.runConf.PythonRunConfHandler;
import rw.settings.ProjectState;
import rw.settings.ProjectSettings;
import rw.tests.BaseMockedTestCase;
import rw.tests.fixtures.*;
import rw.util.EnvUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class TestProjectSettings extends BaseMockedTestCase {
    CakeshopFixture cakeshop;
    SourceRootFixture sourceRootFixture;
    AnAction action;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        PackageFixture packageFixture = new PackageFixture("0.7.12");
        this.cakeshop = new CakeshopFixture(this.getProject());
        this.cakeshop.start();
        this.sourceRootFixture = new SourceRootFixture(this.cakeshop.getRunConf().getModule());

        this.action = ActionManager.getInstance().getAction(RunWithReloadium.ID);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.cakeshop.stop();
        this.sourceRootFixture.stop();
        super.tearDown();
    }

    @Test
    public void testStateEquals() {
        ProjectState state1 = new ProjectState();
        ProjectState state2 = new ProjectState();

        assertThat(state1).isEqualTo(state2);

        state2.reloadiumPath = List.of("1", "2");
        assertThat(state1).isNotEqualTo(state2);

        state2 = new ProjectState();
        state2.watchCwd = !state2.watchCwd;
        assertThat(state1).isNotEqualTo(state2);

        state2 = new ProjectState();
        state2.watchSourceRoots = !state2.watchSourceRoots;
        assertThat(state1).isNotEqualTo(state2);

        state2 = new ProjectState();
        state2.printLogo = !state2.printLogo;
        assertThat(state1).isNotEqualTo(state2);

        state2 = new ProjectState();
        state2.cache = !state2.cache;
        assertThat(state1).isNotEqualTo(state2);

        state2 = new ProjectState();
        state2.verbose = !state2.verbose;
        assertThat(state1).isNotEqualTo(state2);

        state2 = new ProjectState();
        state2.debuggerSpeedups = !state2.debuggerSpeedups;
        assertThat(state1).isNotEqualTo(state2);
    }

    @Test
    public void testDebuggerSpeedups() {
        ProjectState stateIn = new ProjectState();
        stateIn.debuggerSpeedups = !stateIn.debuggerSpeedups;
        ProjectSettings.getInstance(this.getProject()).loadState(stateIn);

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        this.action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        String env = runConf.getEnvs().get(PythonRunConfHandler.DEBUGGER_SPEEDUPS_ENV);
        assertThat(env).isEqualTo(EnvUtils.boolToEnv(stateIn.debuggerSpeedups));
    }

    @Test
    public void testVerbose() {
        ProjectState stateIn = new ProjectState();
        stateIn.verbose = !stateIn.verbose;
        ProjectSettings.getInstance(this.getProject()).loadState(stateIn);

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        this.action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        String env = runConf.getEnvs().get(PythonRunConfHandler.VERBOSE_ENV);
        assertThat(env).isEqualTo(EnvUtils.boolToEnv(stateIn.verbose));
    }

    @Test
    public void testPrintLogo() {
        ProjectState stateIn = new ProjectState();
        stateIn.printLogo = !stateIn.printLogo;
        ProjectSettings.getInstance(this.getProject()).loadState(stateIn);

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        this.action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        String env = runConf.getEnvs().get(PythonRunConfHandler.PRINT_LOGO_ENV);
        assertThat(env).isEqualTo(EnvUtils.boolToEnv(stateIn.printLogo));
    }

    @Test
    public void testCacheEnabled() {
        ProjectState stateIn = new ProjectState();
        stateIn.cache = !stateIn.cache;
        ProjectSettings.getInstance(this.getProject()).loadState(stateIn);

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        this.action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        String env = runConf.getEnvs().get(PythonRunConfHandler.CACHE_ENV);
        assertThat(env).isEqualTo(EnvUtils.boolToEnv(stateIn.cache));
    }

    @Test
    public void testWatchCwd() {
        ProjectState stateIn = new ProjectState();
        stateIn.watchCwd = !stateIn.watchCwd;
        ProjectSettings.getInstance(this.getProject()).loadState(stateIn);

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        this.action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        String env = runConf.getEnvs().get(PythonRunConfHandler.WATCHCWD_ENV);
        assertThat(env).isEqualTo(EnvUtils.boolToEnv(stateIn.watchCwd));
    }

    @Test
    public void testWatchSourceRoots() throws Exception {
        ProjectState stateIn = new ProjectState();
        stateIn.watchSourceRoots = true;
        ProjectSettings.getInstance(this.getProject()).loadState(stateIn);

        this.sourceRootFixture.start();

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        this.action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        String env = runConf.getEnvs().get(PythonRunConfHandler.RELOADIUMPATH_ENV);
        assertThat(env).isEqualTo(this.sourceRootFixture.getSrcRoot().toNioPath().toString());
    }

    @Test
    public void testNotWatchSourceRoots() throws Exception {
        ProjectState stateIn = new ProjectState();
        stateIn.watchSourceRoots = false;
        ProjectSettings.getInstance(this.getProject()).loadState(stateIn);

        this.sourceRootFixture.start();

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        this.action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        String env = runConf.getEnvs().get(PythonRunConfHandler.RELOADIUMPATH_ENV);
        assertThat(env).isEqualTo("");
    }

    @Test
    public void testEmptyWorkingDir() throws Exception {
        PythonRunConfiguration runConf = this.cakeshop.getRunConf();
        runConf.setWorkingDirectory("");

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        this.action.actionPerformed(event);

        String env = runConf.getEnvs().get(PythonRunConfHandler.RELOADIUMPATH_ENV);
        assertThat(env).isEqualTo(this.getProject().getBasePath());
    }

    @Test
    public void testRelodiumPath() throws Exception {
        String pathSep = System.getProperty("path.separator");

        ProjectState stateIn = new ProjectState();
        stateIn.reloadiumPath = List.of("/cakeshop/cake", "/cakeshop/cookie.py");
        ProjectSettings.getInstance(this.getProject()).loadState(stateIn);

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        this.action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        String env = runConf.getEnvs().get(PythonRunConfHandler.RELOADIUMPATH_ENV);
        assertThat(env).isEqualTo(String.join(pathSep, stateIn.reloadiumPath));
    }

    @Test
    public void testProfile() throws Exception {
        ProjectState stateIn = new ProjectState();
        stateIn.profile = !stateIn.profile;
        ProjectSettings.getInstance(this.getProject()).loadState(stateIn);

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        this.action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        String env = runConf.getEnvs().get(PythonRunConfHandler.TIME_PROFILE_ENV);
        assertThat(env).isEqualTo(EnvUtils.boolToEnv(stateIn.profile));
    }
}
