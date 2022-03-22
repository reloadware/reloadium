package rw.handler.sdk;

import com.intellij.openapi.projectRoots.Sdk;
import rw.config.Config;

import java.io.File;


public class UnixSdkHandler extends BaseSdkHandler {
    public UnixSdkHandler(Sdk sdk) {
        super(sdk);
    }

    public File getPackageDir() {
        if (!this.isSdkVersionSupported()) {
            return Config.get().getPackagePythonVersionDir(Config.get().supportedVersions[0]).toFile();
        }
        return Config.get().getPackagePythonVersionDir(this.getVersion()).toFile();
    }
}
