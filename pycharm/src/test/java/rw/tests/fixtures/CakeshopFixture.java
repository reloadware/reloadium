package rw.tests.fixtures;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunManagerConfig;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.compound.CompoundRunConfiguration;
import com.intellij.execution.compound.CompoundRunConfigurationType;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.jetbrains.python.run.PythonConfigurationType;
import com.jetbrains.python.run.PythonRunConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;


public class CakeshopFixture {
    public String PYTHON_VERSION = "3.9";
    protected RunnerAndConfigurationSettings settings;
    protected PythonRunConfiguration runConf;
    protected RunnerAndConfigurationSettings nonPySettings;
    protected CompoundRunConfiguration nonPyConf;
    protected File source;
    protected File venvDir;
    protected Path root;
    protected SdkFixture sdkFixture;
    protected Project project;

    public CakeshopFixture(Project project) throws Exception {
        this.project = project;
    }

    public void setUp() throws Exception {
        // Create source
        this.root = Path.of(Files.createTempDirectory("cakeshop").toFile().getAbsolutePath());
        this.sdkFixture = new SdkFixture(this.root.toFile(), this.PYTHON_VERSION);

        this.source = Path.of(root.toString(), "main.py").toFile();
        String body = Files.readString(Path.of(this.getClass().getClassLoader().getResource("cakeshop.py").getFile()));
        Files.writeString(this.source.toPath(), body);

        // Create settings
        RunManager runManager = RunManager.getInstance(project);
        this.settings = runManager.createConfiguration("testConfig", PythonConfigurationType.class);
        runManager.addConfiguration(this.settings);
        runManager.setSelectedConfiguration(this.settings);

        // Create runConf
        this.runConf = (PythonRunConfiguration) this.settings.getConfiguration();
        this.runConf.setWorkingDirectory(root.toString());
        this.runConf.setScriptName(this.source.getName());

        // Create non Python settings
        this.nonPySettings = runManager.createConfiguration("testNonPyConfig", CompoundRunConfigurationType.class);
        runManager.addConfiguration(this.nonPySettings);
        this.nonPyConf = (CompoundRunConfiguration) this.nonPySettings.getConfiguration();

        // Create sdk
        this.venvDir = new File(this.root.toString(), ".venv");
        this.sdkFixture.start();
        this.runConf.setSdkHome(this.sdkFixture.getSdkHome().toString());

        RunManagerConfig config = RunManagerImpl.getInstanceImpl(this.project).getConfig();
        config.setRestartRequiresConfirmation(false);
    }

    public void tearDown() {
        RunManager runManager = RunManager.getInstance(project);

        runManager.removeConfiguration(this.settings);
        runManager.removeConfiguration(this.nonPySettings);
        runManager.setSelectedConfiguration(null);
        this.sdkFixture.stop();
    }

    public RunnerAndConfigurationSettings getSettings() {
        return this.settings;
    }

    public RunnerAndConfigurationSettings getNonPySettings() {
        return this.nonPySettings;
    }

    public PythonRunConfiguration getRunConf() {
        return this.runConf;
    }

    public Path getRoot() {
        return this.root;
    }

    public Sdk getSdk() {
        return this.sdkFixture.getSdk();
    }
}
