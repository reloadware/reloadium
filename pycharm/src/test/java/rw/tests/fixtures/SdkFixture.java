package rw.tests.fixtures;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.python.sdk.PythonSdkType;
import rw.ai.tests.PythonTestUtil;
import rw.ai.tests.fixtures.PythonMockSdk;
import rw.util.OsType;

import java.io.File;


public class SdkFixture {
    protected Sdk sdk;
    protected String version;

    public SdkFixture(String version) {
        this.version = version;
    }

    public void start() throws Exception {
        this.sdk = PythonMockSdk.create(PythonTestUtil.getTestDataPath() + "/" + "python-%s".formatted(this.version));
        WriteAction.runAndWait(() -> SdkConfigurationUtil.addSdk(this.sdk));
    }

    public void stop() {
        WriteAction.runAndWait(() -> SdkConfigurationUtil.removeSdk(this.sdk));
    }

    public Sdk getSdk() {
        return this.sdk;
    }
}
