package rw.action;

import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import rw.icons.Icons;


public class DebugWithReloadium extends WithReloaderBase implements DumbAware {
    private static final Logger LOGGER = Logger.getInstance(DebugWithReloadium.class);

    public static String ID = "DebugWithReloadium";

    DebugWithReloadium() {
        super();
        this.runType = RunType.DEBUG;
    }

    @Override
    void setRunningIcon(AnActionEvent e) {
        e.getPresentation().setIcon(Icons.RestartDebugger);
    }
    void setNotRunningIcon(AnActionEvent e) {
        e.getPresentation().setIcon(Icons.Debug);
    }

    @Override
    protected Executor getExecutor() {
        return DefaultDebugExecutor.getDebugExecutorInstance();
    }
}
