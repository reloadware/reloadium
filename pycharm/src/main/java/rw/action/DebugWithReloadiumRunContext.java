package rw.action;

import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import rw.icons.Icons;


public class DebugWithReloadiumRunContext extends ContextPopupAction {
    public static String ID = "DebugWithReloadiumRunContext";

    DebugWithReloadiumRunContext() {
        super();
        this.runType = RunType.DEBUG;
    }

    @Override
    void setRunningIcon(AnActionEvent e) {
        e.getPresentation().setIcon(Icons.Debug);
    }

    @Override
    void setNotRunningIcon(AnActionEvent e) {
        e.getPresentation().setIcon(Icons.Debug);
    }

    @Override
    public Executor getExecutor() {
        return DefaultDebugExecutor.getDebugExecutorInstance();
    }
}
