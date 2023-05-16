package rw.consts;

import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.actionSystem.DataKey;

public class DataKeys {
    public static final DataKey<ExecutionEnvironment> EXECUTION_ENVIRONMENT = DataKey.create("executionEnvironment");
}
