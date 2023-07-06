package rw.tests.fixtures;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunManagerConfig;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.compound.CompoundRunConfiguration;
import com.intellij.execution.compound.CompoundRunConfigurationType;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.TempDirTestFixture;
import com.intellij.testFramework.fixtures.impl.LightTempDirTestFixtureImpl;
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
    protected VirtualFile root;
    protected SdkFixture sdkFixture;
    protected Project project;
    protected CodeInsightTestFixture f;

    public CakeshopFixture(CodeInsightTestFixture f) throws Exception {
        this.f = f;
        this.project = f.getProject();
    }

    public void setUp() throws Exception {
        // Create source
        this.root = this.f.getTempDirFixture().findOrCreateDir("cakeshop");
        this.sdkFixture = new SdkFixture(this.PYTHON_VERSION);

        // Create settings
        RunManager runManager = RunManager.getInstance(project);
        this.settings = runManager.createConfiguration("testConfig", PythonConfigurationType.class);
        runManager.addConfiguration(this.settings);
        runManager.setSelectedConfiguration(this.settings);

        // Create runConf
        this.runConf = (PythonRunConfiguration) this.settings.getConfiguration();
        this.runConf.setWorkingDirectory(this.root.toString());
        this.runConf.setScriptName("cakeshop.py");

        // Create non Python settings
        this.nonPySettings = runManager.createConfiguration("testNonPyConfig", CompoundRunConfigurationType.class);
        runManager.addConfiguration(this.nonPySettings);
        this.nonPyConf = (CompoundRunConfiguration) this.nonPySettings.getConfiguration();

        // Create sdk
        this.sdkFixture.start();
        this.runConf.setSdkHome(this.sdkFixture.getSdk().getHomePath());

        RunManagerConfig config = RunManagerImpl.getInstanceImpl(this.project).getConfig();
        config.setRestartRequiresConfirmation(false);
    }

    public void tearDown() throws Exception {
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

    public VirtualFile getRoot() {
        return this.root;
    }

    public Sdk getSdk() {
        return this.sdkFixture.getSdk();
    }
}
