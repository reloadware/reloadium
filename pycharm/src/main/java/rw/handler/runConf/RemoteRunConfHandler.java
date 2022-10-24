package rw.handler.runConf;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.process.BaseProcessHandler;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.remote.*;
import com.jetbrains.python.remote.PyRemoteSdkAdditionalDataBase;
import org.jetbrains.annotations.NotNull;
import rw.action.RunType;
import rw.audit.RwSentry;
import rw.config.Config;
import rw.config.ConfigManager;


public class RemoteRunConfHandler extends PythonRunConfHandler {

    public RemoteRunConfHandler(RunConfiguration runConf) {
        super(runConf);
    }

    public void onProcessStarted(RunContentDescriptor descriptor) {
        try {
            RemoteSshProcess process = (RemoteSshProcess)((BaseProcessHandler) descriptor.getProcessHandler()).getProcess();
            PyRemoteSdkAdditionalDataBase additionalData = ((PyRemoteSdkAdditionalDataBase) this.runConf.getSdk().getSdkAdditionalData());
            RemoteSdkCredentials credentials = additionalData.getRemoteSdkCredentials(this.runConf.getProject(), true);

            process.addRemoteTunnel(this.session.getPort(), credentials.getHost(), this.session.getPort());
        } catch (Exception e) {
            RwSentry.get().captureException(e);
        }
    }

    @Override
    public void beforeRun(RunType runType) {
        super.beforeRun(runType);

        ConfigManager.get().createIfNotExists();
        Config config = ConfigManager.get().getConfig();

        this.runConf.getEnvs().put("RW_USERID", config.user.uuid);
    }

    @NotNull
    @Override
    public String convertPathToLocal(@NotNull String remotePath) {
        if (this.runConf.getMappingSettings() == null){
            return remotePath;
        }

        String ret = this.runConf.getMappingSettings().convertToLocal(remotePath);
        return ret;
    }

    @NotNull
    @Override
    public String convertPathToRemote(@NotNull String localPath) {
        if (this.runConf.getMappingSettings() == null){
            return localPath;
        }

        String ret = this.runConf.getMappingSettings().convertToRemote(localPath);
        return ret;
    }
}
