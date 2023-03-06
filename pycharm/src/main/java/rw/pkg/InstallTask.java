package rw.pkg;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.audit.RwSentry;
import rw.consts.Const;

public class InstallTask extends Task.Backgroundable {
    @Nullable
    protected PackageManager packageManager;

    @Nullable
    PackageManager.Listener listener;

    boolean fail;

    InstallTask(@NotNull PackageManager packageManager,
                @Nullable PackageManager.Listener listener,
                boolean fail) {
        super(null, Const.get().msgs.INSTALLING_PACKAGE);
        this.packageManager = packageManager;
        this.listener = listener;
        this.fail = fail;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        indicator.setText(this.getTaskText());
        runTask(indicator);
    }

    @Override
    public void onCancel() {
        if (this.listener != null) {
            this.listener.cancelled();
        }
    }

    @NotNull
    protected String getTaskText() {
        return Const.get().msgs.INSTALLING_PACKAGE;
    }

    protected void runTask(@NotNull ProgressIndicator indicator) {
        if (!this.packageManager.shouldInstall()) {
            this.packageManager.installing = false;
            return;
        }
        try {
            this.packageManager.install();
            if (this.listener != null) {
                this.listener.success();
            }
        } catch (Exception e) {
            if (this.listener != null) {
                this.listener.fail(e);
            }
            RwSentry.get().captureException(e, this.fail);
        } finally {
            this.packageManager.installing = false;
        }
    }
}
