package rw.handler.runConf;

import com.intellij.execution.configurations.RunConfiguration;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import org.jetbrains.annotations.Nullable;

public abstract class RunConfHandlerFactory {
    public static @Nullable BaseRunConfHandler factory(RunConfiguration runConf) {
        if (AbstractPythonRunConfiguration.class.isAssignableFrom(runConf.getClass())){
            return new PythonRunConfHandler(runConf);
        }
        else {
            return null;
        }

    }
}
