package rw.action;

import com.intellij.execution.Executor;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.consts.DataKeys;
import rw.icons.Icons;


public class RerunDebugWithReloadium extends WithReloaderBase {
    private static final Logger LOGGER = Logger.getInstance(RerunDebugWithReloadium.class);
    public static String ID = "RerunDebugWithReloadium";

    RerunDebugWithReloadium() {
        super();
        this.runType = RunType.DEBUG;
    }

    @Override
    @Nullable
    protected RunnerAndConfigurationSettings getConfiguration(@NotNull AnActionEvent e) {
        ExecutionEnvironment environment = e.getData(DataKeys.EXECUTION_ENVIRONMENT);

        if (environment == null) {
            return null;
        }

        RunnerAndConfigurationSettings ret = environment.getRunnerAndConfigurationSettings();
        return ret;
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
