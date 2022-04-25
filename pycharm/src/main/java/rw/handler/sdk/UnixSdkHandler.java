package rw.handler.sdk;

import com.intellij.openapi.projectRoots.Sdk;
import rw.consts.Const;

import java.io.File;


public class UnixSdkHandler extends BaseSdkHandler {
    public UnixSdkHandler(Sdk sdk) {
        super(sdk);
    }

    public File getPackageDir() {
        if (!this.isSdkVersionSupported()) {
            return Const.get().getPackagePythonVersionDir(Const.get().supportedVersions[0]);
        }
        return Const.get().getPackagePythonVersionDir(this.getVersion());
    }
}
