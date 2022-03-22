package rw.action;

import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import rw.icons.Icons;


public class RunWithReloadiumRunContext extends ContextPopupAction {
    RunWithReloadiumRunContext() {
        super();
        this.runType = RunType.RUN;
    }

    void setRunningIcon(AnActionEvent e) {
        e.getPresentation().setIcon(Icons.Run);
    }
    void setNotRunningIcon(AnActionEvent e) {
        e.getPresentation().setIcon(Icons.Run);
    }

    protected Executor getExecutor() {
        return DefaultRunExecutor.getRunExecutorInstance();
    }
}
