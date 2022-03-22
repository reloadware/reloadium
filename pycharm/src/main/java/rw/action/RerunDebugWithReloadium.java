package rw.action;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKey;
import org.jetbrains.annotations.NotNull;

class ExecutionDataKeys {
  public static final DataKey<ExecutionEnvironment> EXECUTION_ENVIRONMENT = DataKey.create("executionEnvironment");
}


public class RerunDebugWithReloadium extends DebugWithReloadium {
    @Override
    protected RunnerAndConfigurationSettings getConfiguration(@NotNull AnActionEvent e) {
        ExecutionEnvironment environment = e.getData(ExecutionDataKeys.EXECUTION_ENVIRONMENT);
        RunnerAndConfigurationSettings ret = environment.getRunnerAndConfigurationSettings();
        return ret;
    }
}
