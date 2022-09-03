package rw.handler.sdk;

import com.intellij.openapi.projectRoots.Sdk;
import com.jetbrains.python.psi.LanguageLevel;
import com.jetbrains.python.sdk.flavors.PythonSdkFlavor;
import rw.consts.Const;

import java.io.File;
import java.util.Arrays;


public class SdkHandler {
    Sdk sdk;

    public SdkHandler(Sdk sdk) {
        this.sdk = sdk;
    }

    public File getPackageDir() {
        if (!this.isSdkVersionSupported()) {
            return Const.get().getPackagePythonVersionDir(Const.get().supportedVersions[0]);
        }
        return Const.get().getPackagePythonVersionDir(this.getVersion());
    }

    public String getVersion() {
        PythonSdkFlavor flavor = PythonSdkFlavor.getFlavor(this.sdk);
        assert flavor != null;

        LanguageLevel level = flavor.getLanguageLevel(this.sdk);
        String ret = level.toPythonVersion();
        return ret;
    }

    public boolean isSdkVersionSupported() {
        String sdkVersion = this.getVersion();

        if(sdkVersion == null) {
            return false;
        }

        boolean ret = Arrays.asList(Const.get().supportedVersions).contains(this.getVersion());
        return ret;
    }

    public boolean isValid() {
        if (this.sdk.getHomePath() == null) {
            return false;
        }

        boolean ret = true;
        ret &= PythonSdkFlavor.getFlavor(this.sdk) != null;
        return ret;
    }

    public Sdk getSdk() {
        return this.sdk;
    }
}
