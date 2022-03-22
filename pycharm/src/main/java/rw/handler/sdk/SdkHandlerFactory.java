package rw.handler.sdk;

import com.intellij.openapi.projectRoots.Sdk;
import com.jetbrains.python.sdk.PythonSdkType;
import com.jetbrains.python.sdk.flavors.PythonSdkFlavor;
import com.jetbrains.python.sdk.flavors.UnixPythonSdkFlavor;
import com.jetbrains.python.sdk.flavors.WinPythonSdkFlavor;
import org.jetbrains.annotations.Nullable;


abstract public class SdkHandlerFactory {
    public static @Nullable BaseSdkHandler factory(@Nullable Sdk sdk) {
        if (sdk == null) {
            return null;
        }

        if(sdk.getSdkType() != PythonSdkType.getInstance()) {
            return null;
        }

        assert sdk.getHomePath() != null;

        if (sdk.getHomePath().endsWith(".exe")){
            return new WinSdkHandler(sdk);
        }
        else {
            return new UnixSdkHandler(sdk);
        }
    }
}
