package rw.remote;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.projectRoots.Sdk;
import com.jetbrains.python.target.PyTargetAwareAdditionalData;

public class RemoteUtils {
    private static final Logger LOGGER = Logger.getInstance(RemoteUtils.class);

    static public boolean isSdkServerRemote(Sdk sdk) {
        if (!(sdk.getSdkAdditionalData() instanceof PyTargetAwareAdditionalData)) {
            return false;
        }

        PyTargetAwareAdditionalData additionalData = (PyTargetAwareAdditionalData) sdk.getSdkAdditionalData();

        if (additionalData.getTargetEnvironmentConfiguration().getClass().getName().equals(
                "com.jetbrains.plugins.remotesdk.target.webDeployment.WebDeploymentTargetEnvironmentConfiguration"
        )) {
            return true;
        }

        return false;
    }
}
