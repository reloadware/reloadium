package rw.action;

import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import rw.dialogs.DialogFactory;
import rw.icons.Icons;
import rw.service.Service;


public class RunWithReloadium extends WithReloaderBase implements DumbAware {
    private static final Logger LOGGER = Logger.getInstance(RunWithReloadium.class);
    public static String ID = "RunWithReloadium";

    RunWithReloadium() {
        super();
        this.runType = RunType.RUN;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = getEventProject(e);
        boolean result = DialogFactory.get().showFirstRunDialog(project);

        Service.get().onRun();

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

    public Executor getExecutor() {
        return DefaultRunExecutor.getRunExecutorInstance();
    }
}
