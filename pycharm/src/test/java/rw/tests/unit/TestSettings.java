package rw.tests.unit;

import com.intellij.execution.RunManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.testFramework.TestActionEvent;
import com.jetbrains.python.run.PythonRunConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import rw.action.RunWithReloadium;
import rw.pkg.Architecture;
import rw.settings.PluginState;
import rw.settings.Settings;
import rw.settings.SettingsConfigurable;
import rw.tests.BaseMockedTestCase;
import rw.tests.fixtures.*;
import rw.util.EnvUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestSettings extends BaseMockedTestCase {
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
        PluginState state1 = new PluginState();
        PluginState state2 = new PluginState();

        assertThat(state1).isEqualTo(state2);

        state2.reloadiumPath = List.of("1", "2");
        assertThat(state1).isNotEqualTo(state2);

        state2 = new PluginState();
        state2.watchCwd = !state2.watchCwd;
        assertThat(state1).isNotEqualTo(state2);

        state2 = new PluginState();
        state2.watchSourceRoots = !state2.watchSourceRoots;
        assertThat(state1).isNotEqualTo(state2);

        state2 = new PluginState();
        state2.printLogo = !state2.printLogo;
        assertThat(state1).isNotEqualTo(state2);

        state2 = new PluginState();
        state2.cacheEnabled = !state2.cacheEnabled;
        assertThat(state1).isNotEqualTo(state2);

        state2 = new PluginState();
        state2.verbose = !state2.verbose;
        assertThat(state1).isNotEqualTo(state2);

        state2 = new PluginState();
        state2.debuggerSpeedups = !state2.debuggerSpeedups;
        assertThat(state1).isNotEqualTo(state2);
    }

    @Test
    public void testDebuggerSpeedups() {
        PluginState stateIn = new PluginState();
        stateIn.debuggerSpeedups = !stateIn.debuggerSpeedups;
        Settings.getInstance(this.getProject()).loadState(stateIn);

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        this.action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        String env = runConf.getEnvs().get("RW_DEBUGGERSPEEDUPS");
        assertThat(env).isEqualTo(EnvUtils.boolToEnv(stateIn.debuggerSpeedups));
    }

    @Test
    public void testVerbose() {
        PluginState stateIn = new PluginState();
        stateIn.verbose = !stateIn.verbose;
        Settings.getInstance(this.getProject()).loadState(stateIn);

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        this.action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        String env = runConf.getEnvs().get("RW_VERBOSE");
        assertThat(env).isEqualTo(EnvUtils.boolToEnv(stateIn.verbose));
    }

    @Test
    public void testPrintLogo() {
        PluginState stateIn = new PluginState();
        stateIn.printLogo = !stateIn.printLogo;
        Settings.getInstance(this.getProject()).loadState(stateIn);

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        this.action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        String env = runConf.getEnvs().get("RW_PRINTLOGO");
        assertThat(env).isEqualTo(EnvUtils.boolToEnv(stateIn.printLogo));
    }

    @Test
    public void testCacheEnabled() {
        PluginState stateIn = new PluginState();
        stateIn.cacheEnabled = !stateIn.cacheEnabled;
        Settings.getInstance(this.getProject()).loadState(stateIn);

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        this.action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        String env = runConf.getEnvs().get("RW_CACHE");
        assertThat(env).isEqualTo(EnvUtils.boolToEnv(stateIn.cacheEnabled));
    }

    @Test
    public void testWatchCwd() {
        PluginState stateIn = new PluginState();
        stateIn.watchCwd = !stateIn.watchCwd;
        Settings.getInstance(this.getProject()).loadState(stateIn);

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        this.action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        String env = runConf.getEnvs().get("RW_WATCHCWD");
        assertThat(env).isEqualTo(EnvUtils.boolToEnv(stateIn.watchCwd));
    }

    @Test
    public void testWatchSourceRoots() throws Exception {
        PluginState stateIn = new PluginState();
        stateIn.watchSourceRoots = true;
        Settings.getInstance(this.getProject()).loadState(stateIn);

        this.sourceRootFixture.start();

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        this.action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        String env = runConf.getEnvs().get("RELOADIUMPATH");
        assertThat(env).isEqualTo(this.sourceRootFixture.getSrcRoot().toNioPath().toString());
    }

    @Test
    public void testNotWatchSourceRoots() throws Exception {
        PluginState stateIn = new PluginState();
        stateIn.watchSourceRoots = false;
        Settings.getInstance(this.getProject()).loadState(stateIn);

        this.sourceRootFixture.start();

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        this.action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        String env = runConf.getEnvs().get("RELOADIUMPATH");
        assertThat(env).isEqualTo("");
    }

    @Test
    public void testRelodiumPath() throws Exception {
        String pathSep = System.getProperty("path.separator");

        PluginState stateIn = new PluginState();
        stateIn.reloadiumPath = List.of("/cakeshop/cake", "/cakeshop/cookie.py");
        Settings.getInstance(this.getProject()).loadState(stateIn);

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        this.action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        String env = runConf.getEnvs().get("RELOADIUMPATH");
        assertThat(env).isEqualTo(String.join(pathSep, stateIn.reloadiumPath));
    }
}
