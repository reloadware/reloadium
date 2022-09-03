package rw.tests.fixtures;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.jetbrains.python.sdk.PythonSdkType;
import rw.pkg.Architecture;
import rw.util.OsType;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;


public class WinSdkFixture extends SdkFixture {
    WinFixture winFixture;

    public WinSdkFixture(File projectRoot, String version, Architecture architecture) {
        super(projectRoot, version);
        this.winFixture = new WinFixture();
    }

    public void start() throws Exception {
        this.winFixture.start();

        this.venvDir = new File(this.projectRoot.toString(), String.format(".venv%s-%s", this.version, OsType.DETECTED));
        this.sdkHome = new File(this.venvDir, "/bin/python.exe");
        new File(this.sdkHome.getParent()).mkdirs();
        this.sdkHome.createNewFile();
        this.sdk = new ProjectJdkImpl("cakeshop", PythonSdkType.getInstance(), this.sdkHome.toString(),
                String.format("Python %s", this.version));
        WriteAction.runAndWait(() -> SdkConfigurationUtil.addSdk(this.sdk));

        byte[] interpreterContent;
        interpreterContent = Files.readAllBytes(Path.of(this.getClass().getClassLoader().getResource("python64.exe").getFile()));

        Files.write(this.getSdkHome().toPath(), interpreterContent);
    }

    public void stop() {
        WriteAction.runAndWait(() -> SdkConfigurationUtil.removeSdk(this.sdk));
        this.winFixture.stop();
    }

    public Sdk getSdk() {
        return this.sdk;
    }
    public File getSdkHome() {
        return this.sdkHome;
    }
}
