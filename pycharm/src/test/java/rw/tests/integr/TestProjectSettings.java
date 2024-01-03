package rw.tests.integr;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.testFramework.TestActionEvent;
import com.jetbrains.python.run.PythonRunConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import rw.action.RunType;
import rw.action.RunWithReloadium;
import rw.action.WithReloaderBase;
import rw.handler.PythonRunConfHandler;
import rw.handler.RunConfHandlerFactory;
import rw.settings.ProjectSettings;
import rw.settings.ProjectState;
import rw.tests.BaseTestCase;
import rw.tests.fixtures.CakeshopFixture;
import rw.tests.fixtures.PackageFixture;
import rw.tests.fixtures.SourceRootFixture;
import rw.util.EnvUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class TestProjectSettings extends BaseTestCase {
    CakeshopFixture cakeshop;
    SourceRootFixture sourceRootFixture;
    WithReloaderBase action;
    PythonRunConfHandler handler;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        PackageFixture packageFixture = new PackageFixture(this.packageManager, "0.7.12");
        this.cakeshop = new CakeshopFixture(this.f);
        this.cakeshop.setUp();
        this.sourceRootFixture = new SourceRootFixture(this.cakeshop.getRunConf().getModule());
        this.handler = (PythonRunConfHandler) RunConfHandlerFactory.factory(this.cakeshop.getRunConf());
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.cakeshop.tearDown();
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
    }

    @Test
    public void testVerbose() {
        ProjectState stateIn = new ProjectState();
        stateIn.verbose = !stateIn.verbose;
        ProjectSettings.getInstance(this.getProject()).loadState(stateIn);

        this.handler.beforeRun(RunType.DEBUG);
        PythonRunConfiguration runConf = (PythonRunConfiguration)this.handler.getRunConf();

        String env = runConf.getEnvs().get(PythonRunConfHandler.VERBOSE_ENV);
        assertThat(env).isEqualTo(EnvUtils.boolToEnv(stateIn.verbose));
    }

    @Test
    public void testDebugActivated() {
        this.handler.beforeRun(RunType.DEBUG);
        PythonRunConfiguration runConf = (PythonRunConfiguration)this.handler.getRunConf();

        assertThat(runConf.getScriptName()).isEqualTo("cakeshop.py");
        assertThat(runConf.isModuleMode()).isFalse();

        String pythonpath = runConf.getEnvs().get("PYTHONPATH");
        assertThat(pythonpath).isEqualTo(String.valueOf(this.packageManager.getFs().getPackagesRootDir()));

        assertThat(runConf.getInterpreterOptions()).isEqualTo(" -m reloadium_launcher pydev_proxy");
    }

    @Test
    @SetEnvironmentVariable(key = "PYTHONPATH", value = "MyPath")
    public void testSysPythonpathPersisted() {
        this.handler.beforeRun(RunType.DEBUG);
        PythonRunConfiguration runConf = (PythonRunConfiguration)this.handler.getRunConf();

        String pathSep = System.getProperty("path.separator");

        assertThat(runConf.getScriptName()).isEqualTo("cakeshop.py");
        assertThat(runConf.isModuleMode()).isFalse();

        String pythonpath = runConf.getEnvs().get("PYTHONPATH");

        String expected = String.format("%s%sMyPath", this.packageManager.getFs().getPackagesRootDir(), pathSep);
        assertThat(pythonpath.equals(expected)).isTrue();
    }

    @Test
    public void testModuleActionPerformed() {
        this.cakeshop.getRunConf().setModuleMode(true);
        this.cakeshop.getRunConf().setScriptName("main");

        PythonRunConfHandler handler = (PythonRunConfHandler) RunConfHandlerFactory.factory(this.cakeshop.getRunConf());

        handler.beforeRun(RunType.DEBUG);
        PythonRunConfiguration runConf = (PythonRunConfiguration) handler.getRunConf();

        assertThat(runConf.getScriptName()).isEqualTo("main");
        assertThat(runConf.isModuleMode()).isTrue();
        assertThat(runConf.getEnvs().get("PYTHONPATH").isBlank()).isFalse();
        assertThat(runConf.getInterpreterOptions()).isEqualTo(" -m reloadium_launcher pydev_proxy");
    }

    @Test
    public void testRunActivated() {
        this.handler.beforeRun(RunType.RUN);
        PythonRunConfiguration runConf = (PythonRunConfiguration)this.handler.getRunConf();

        assertThat(runConf.getScriptName()).isEqualTo("cakeshop.py");
        assertThat(runConf.isModuleMode()).isFalse();

        String pythonpath = runConf.getEnvs().get("PYTHONPATH");
        assertThat(pythonpath).isEqualTo(String.valueOf(this.packageManager.getFs().getPackagesRootDir()));

        assertThat(runConf.getInterpreterOptions()).isEqualTo(" -m reloadium_launcher run");
    }

    @Test
    public void testPrintLogo() {
        ProjectState stateIn = new ProjectState();
        stateIn.printLogo = !stateIn.printLogo;
        ProjectSettings.getInstance(this.getProject()).loadState(stateIn);

        this.handler.beforeRun(RunType.DEBUG);
        PythonRunConfiguration runConf = (PythonRunConfiguration)this.handler.getRunConf();

        String env = runConf.getEnvs().get(PythonRunConfHandler.PRINT_LOGO_ENV);
        assertThat(env).isEqualTo(EnvUtils.boolToEnv(stateIn.printLogo));
    }

    @Test
    public void testCacheEnabled() {
        ProjectState stateIn = new ProjectState();
        stateIn.cache = !stateIn.cache;
        ProjectSettings.getInstance(this.getProject()).loadState(stateIn);

        this.handler.beforeRun(RunType.DEBUG);
        PythonRunConfiguration runConf = (PythonRunConfiguration)this.handler.getRunConf();

        String env = runConf.getEnvs().get(PythonRunConfHandler.CACHE_ENV);
        assertThat(env).isEqualTo(EnvUtils.boolToEnv(stateIn.cache));
    }

    @Test
    public void testWatchCwd() {
        ProjectState stateIn = new ProjectState();
        stateIn.watchCwd = !stateIn.watchCwd;
        ProjectSettings.getInstance(this.getProject()).loadState(stateIn);

        this.handler.beforeRun(RunType.DEBUG);
        PythonRunConfiguration runConf = (PythonRunConfiguration)this.handler.getRunConf();

        String env = runConf.getEnvs().get(PythonRunConfHandler.WATCHCWD_ENV);
        assertThat(env).isEqualTo(EnvUtils.boolToEnv(stateIn.watchCwd));
    }

    @Test
    public void testWatchSourceRoots() throws Exception {
        ProjectState stateIn = new ProjectState();
        stateIn.watchSourceRoots = true;
        ProjectSettings.getInstance(this.getProject()).loadState(stateIn);

        this.sourceRootFixture.start();

        this.handler.beforeRun(RunType.DEBUG);
        PythonRunConfiguration runConf = (PythonRunConfiguration)this.handler.getRunConf();

        String env = runConf.getEnvs().get(PythonRunConfHandler.RELOADIUMPATH_ENV);
        assertThat(env).isEqualTo(this.sourceRootFixture.getSrcRoot().toNioPath().toString());
    }

    @Test
    public void testNotWatchSourceRoots() throws Exception {
        ProjectState stateIn = new ProjectState();
        stateIn.watchSourceRoots = false;
        ProjectSettings.getInstance(this.getProject()).loadState(stateIn);

        this.sourceRootFixture.start();

        this.handler.beforeRun(RunType.DEBUG);
        PythonRunConfiguration runConf = (PythonRunConfiguration)this.handler.getRunConf();

        String env = runConf.getEnvs().get(PythonRunConfHandler.RELOADIUMPATH_ENV);
        assertThat(env).isEqualTo("");
    }

    @Test
    public void testEmptyWorkingDir() throws Exception {
        PythonRunConfiguration sourceRunConf = this.cakeshop.getRunConf();
        sourceRunConf.setWorkingDirectory("");

        PythonRunConfHandler handler = (PythonRunConfHandler) RunConfHandlerFactory.factory(sourceRunConf);
        handler.beforeRun(RunType.DEBUG);

        PythonRunConfiguration runConf = (PythonRunConfiguration)handler.getRunConf();

        String env = runConf.getEnvs().get(PythonRunConfHandler.RELOADIUMPATH_ENV);
        assertThat(env).isEqualTo(this.getProject().getBasePath());
    }

    @Test
    public void testRelodiumPath() throws Exception {
        String pathSep = System.getProperty("path.separator");

        ProjectState stateIn = new ProjectState();
        stateIn.reloadiumPath = List.of("/cakeshop/cake", "/cakeshop/cookie.py");
        ProjectSettings.getInstance(this.getProject()).loadState(stateIn);

        this.handler.beforeRun(RunType.DEBUG);
        PythonRunConfiguration runConf = (PythonRunConfiguration)this.handler.getRunConf();

        String env = runConf.getEnvs().get(PythonRunConfHandler.RELOADIUMPATH_ENV);
        assertThat(env).isEqualTo(String.join(pathSep, stateIn.reloadiumPath));
    }
}
