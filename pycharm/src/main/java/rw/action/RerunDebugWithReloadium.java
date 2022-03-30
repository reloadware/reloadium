package rw.action;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

class ExecutionDataKeys {
  public static final DataKey<ExecutionEnvironment> EXECUTION_ENVIRONMENT = DataKey.create("executionEnvironment");
}


public class RerunDebugWithReloadium extends DebugWithReloadium {
    private static final Logger LOGGER = Logger.getInstance(RerunDebugWithReloadium.class);

    @Override
    protected RunnerAndConfigurationSettings getConfiguration(@NotNull AnActionEvent e) {
        ExecutionEnvironment environment = e.getData(ExecutionDataKeys.EXECUTION_ENVIRONMENT);
        RunnerAndConfigurationSettings ret = environment.getRunnerAndConfigurationSettings();
        return ret;
    }
}
