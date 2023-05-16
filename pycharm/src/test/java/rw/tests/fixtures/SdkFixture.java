package rw.tests.fixtures;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.jetbrains.python.sdk.PythonSdkType;
import rw.util.OsType;

import java.io.File;


public class SdkFixture {
    protected File projectRoot;
    protected File venvDir;
    protected File sdkHome;
    protected Sdk sdk;
    protected String version;

    public SdkFixture(File projectRoot, String version) {
        this.projectRoot = projectRoot;
        this.version = version;
    }

    public void start() throws Exception {
        // Create sdk
        this.venvDir = new File(this.projectRoot.toString(), String.format(".venv%s-%s", this.version, OsType.DETECTED));
        this.sdkHome = new File(this.venvDir, "/bin/python");
        new File(this.sdkHome.getParent()).mkdirs();
        this.sdkHome.createNewFile();
        this.sdk = new ProjectJdkImpl("cakeshop", PythonSdkType.getInstance(), this.sdkHome.toString(),
                String.format("Python %s", this.version));
        WriteAction.runAndWait(() -> SdkConfigurationUtil.addSdk(this.sdk));
    }

    public void stop() {
        WriteAction.runAndWait(() -> SdkConfigurationUtil.removeSdk(this.sdk));
    }

    public Sdk getSdk() {
        return this.sdk;
    }

    public File getSdkHome() {
        return this.sdkHome;
    }
}
