package rw.handler.sdk;

import com.intellij.openapi.projectRoots.Sdk;
import com.jetbrains.python.sdk.PythonSdkType;
import org.jetbrains.annotations.Nullable;

import java.io.File;


abstract public class SdkHandlerFactory {
    public static @Nullable
    SdkHandler factory(@Nullable Sdk sdk) {
        if (sdk == null) {
            return null;
        }

        if(sdk.getSdkType() != PythonSdkType.getInstance()) {
            return null;
        }

        assert sdk.getHomePath() != null;
        return new SdkHandler(sdk);
    }
}
