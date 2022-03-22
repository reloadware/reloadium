package rw.action;

import com.intellij.execution.*;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import rw.icons.Icons;


public class RunWithReloadium extends WithReloaderBase {
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
