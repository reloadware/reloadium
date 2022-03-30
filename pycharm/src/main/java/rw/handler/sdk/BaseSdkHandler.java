package rw.handler.sdk;

import com.intellij.openapi.projectRoots.Sdk;
import com.jetbrains.python.psi.LanguageLevel;
import com.jetbrains.python.sdk.flavors.PythonSdkFlavor;
import rw.config.Config;

import java.io.File;
import java.util.Arrays;


abstract public class BaseSdkHandler {
    Sdk sdk;

    public BaseSdkHandler(Sdk sdk) {
        this.sdk = sdk;
    }

    abstract public File getPackageDir();

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

        boolean ret = Arrays.asList(Config.get().supportedVersions).contains(this.getVersion());
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
}
