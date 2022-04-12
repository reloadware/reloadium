package rw.action;

import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import rw.icons.Icons;


public class RunWithReloadium extends WithReloaderBase implements DumbAware {
    private static final Logger LOGGER = Logger.getInstance(RunWithReloadium.class);

    RunWithReloadium() {
        super();
        this.runType = RunType.RUN;
    }

    public static String ID = "RunWithReloadium";

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
