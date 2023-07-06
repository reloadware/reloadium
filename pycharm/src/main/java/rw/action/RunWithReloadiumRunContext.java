package rw.action;

import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import rw.icons.Icons;


public class RunWithReloadiumRunContext extends ContextPopupAction {
    private static final Logger LOGGER = Logger.getInstance(RunWithReloadiumRunContext.class);
    public static String ID = "RunWithReloadiumRunContext";

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

    public Executor getExecutor() {
        return DefaultRunExecutor.getRunExecutorInstance();
    }
}
