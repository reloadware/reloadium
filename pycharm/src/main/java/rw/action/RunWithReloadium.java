package rw.action;

import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import rw.dialogs.DialogFactory;
import rw.dialogs.FirstRunDialog;
import rw.dialogs.TipDialog;
import rw.icons.Icons;


public class RunWithReloadium extends WithReloaderBase implements DumbAware {
    private static final Logger LOGGER = Logger.getInstance(RunWithReloadium.class);

    RunWithReloadium() {
        super();
        this.runType = RunType.RUN;
    }

    public static String ID = "RunWithReloadium";

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = getEventProject(e);
        boolean result = DialogFactory.get().showFirstRunDialog(project);

        if (!result) {
            return;
        }

        super.actionPerformed(e);
    }

    void setRunningIcon(AnActionEvent e) {
        e.getPresentation().setIcon(Icons.Restart);
    }
    void setNotRunningIcon(AnActionEvent e) {
        e.getPresentation().setIcon(Icons.Run);
    }

    protected Executor getExecutor() {
        return DefaultRunExecutor.getRunExecutorInstance();
    }
}
