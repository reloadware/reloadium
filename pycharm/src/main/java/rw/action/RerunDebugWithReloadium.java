package rw.action;

import com.intellij.execution.Executor;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import rw.icons.Icons;

class ExecutionDataKeys {
  public static final DataKey<ExecutionEnvironment> EXECUTION_ENVIRONMENT = DataKey.create("executionEnvironment");
}


public class RerunDebugWithReloadium extends WithReloaderBase {
    private static final Logger LOGGER = Logger.getInstance(RerunDebugWithReloadium.class);

        RerunDebugWithReloadium() {
        super();
        this.runType = RunType.DEBUG;
    }

    @Override
    protected RunnerAndConfigurationSettings getConfiguration(@NotNull AnActionEvent e) {
        ExecutionEnvironment environment = e.getData(ExecutionDataKeys.EXECUTION_ENVIRONMENT);
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
    protected Executor getExecutor() {
        return DefaultDebugExecutor.getDebugExecutorInstance();
    }
}
