package rw.handler;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import org.jetbrains.annotations.NotNull;
import rw.action.RunType;
import rw.config.Config;
import rw.config.ConfigManager;
import rw.consts.Const;
import rw.consts.Stage;

import java.io.File;

import static com.intellij.util.ui.UIUtil.invokeLaterIfNeeded;


abstract public class RemoteRunConfHandler extends PythonRunConfHandler {
    public static final String USER_ID_ENV = "RW_USERID";
    public static final String LICENSE_ENV = "RW_LICENSEKEY";
    public static final String REMOTE_ENV = "RW_REMOTE";

    private boolean warned;

    public RemoteRunConfHandler(RunConfiguration runConf) {
        super(runConf);

        this.warned = false;
    }

    protected File getPackagesRootDir() {
        String ret = "/root/.reloadium/package";

        if (Const.get().stage != Stage.PROD) {
            ret += "_" + Const.get().stage.value;
        }

        return new File(ret);
    }

    @Override
    public void beforeRun(RunType runType) {
        super.beforeRun(runType);

        try(Config config = ConfigManager.get().getConfig(true)) {
            this.runConf.getEnvs().put(USER_ID_ENV, config.user.uuid);
            this.runConf.getEnvs().put(LICENSE_ENV, config.account.licenseKey);
            this.runConf.getEnvs().put(REMOTE_ENV, "True");
        }
    }

    @NotNull
    @Override
    public String convertPathToLocal(@NotNull String remotePath, boolean warnMissing) {
        if (this.runConf.getMappingSettings() == null) {
            if (warnMissing && !this.warned) {
                this.warned = true;
                ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog(this.project, String.format("Path mappings are missing for remote path %s", remotePath), "Missing Remote Path Mappings"));
            }
            return remotePath;
        }

        String ret = this.runConf.getMappingSettings().convertToLocal(remotePath);

        if (warnMissing && ret.equals(remotePath) && !this.warned) {
            this.warned = true;
            ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog(this.project, String.format("Could not convert remote path %s to the local one. " +
                    "Add path mappings to resolve this", remotePath), "Missing Path Mappings"));
        }
        return ret;
    }

    @NotNull
    @Override
    public String convertPathToRemote(@NotNull String localPath, boolean warnMissing) {
        if (this.runConf.getMappingSettings() == null) {
            if (warnMissing && !this.warned) {
                this.warned = true;
                ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog(this.project, String.format("Path mappings are missing for local path %s", localPath), "Missing Local Path Mappings"));
            }
            return localPath;
        }

        String ret = this.runConf.getMappingSettings().convertToRemote(localPath);

        if (warnMissing && ret.equals(localPath) && !this.warned) {
            this.warned = true;
            ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog(this.project, String.format("Could not convert local path %s to the remote one. " +
                    "Add path mappings to resolve this", localPath), "Missing Path Mappings"));
        }
        return ret;
    }
}
