package rw.remote;

import com.intellij.openapi.projectRoots.Sdk;
import com.jetbrains.python.target.PyTargetAwareAdditionalData;
import rw.remote.sftp.SFTPException;

import java.lang.reflect.InvocationTargetException;

public class RemoteUtils {
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

    static public void checkSftpException(InvocationTargetException e) throws SFTPException {
        if (e.getTargetException().getClass().getName().equals("net.schmizz.sshj.sftp.SFTPException")) {
            throw new SFTPException();
        }
    }
}
