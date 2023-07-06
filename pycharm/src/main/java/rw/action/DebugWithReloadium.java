package rw.action;

import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import rw.dialogs.DialogFactory;
import rw.icons.Icons;
import rw.service.Service;


public class DebugWithReloadium extends WithReloaderBase implements DumbAware {
    private static final Logger LOGGER = Logger.getInstance(DebugWithReloadium.class);

    public static String ID = "DebugWithReloadium";

    public DebugWithReloadium() {
        super();
        this.runType = RunType.DEBUG;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = getEventProject(e);
        boolean result = DialogFactory.get().showFirstDebugDialog(project);

        Service.get().onRun();

        if (!result) {
            return;
        }

        super.actionPerformed(e);
    }

    @Override
    void setRunningIcon(AnActionEvent e) {
        e.getPresentation().setIcon(Icons.RestartDebugger);
    }

    void setNotRunningIcon(AnActionEvent e) {
        e.getPresentation().setIcon(Icons.Debug);
    }

    @Override
    public Executor getExecutor() {
        return DefaultDebugExecutor.getDebugExecutorInstance();
    }
}
