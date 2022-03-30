package rw.handler.runConf;

import com.intellij.execution.configurations.RunConfiguration;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import org.jetbrains.annotations.Nullable;
import rw.action.RunType;
import rw.handler.sdk.BaseSdkHandler;
import rw.handler.sdk.SdkHandlerFactory;

public abstract class BaseRunConfHandler {
    AbstractPythonRunConfiguration<?> runConf;
    @Nullable BaseSdkHandler sdkHandler;

    public BaseRunConfHandler(RunConfiguration runConf) {
        this.runConf = (AbstractPythonRunConfiguration<?>)runConf;

        this.sdkHandler = SdkHandlerFactory.factory(this.runConf.getSdk());
    }

    abstract public void beforeRun(RunType runType);
    abstract public void afterRun();
    abstract public boolean isActivated();

    abstract public boolean canRun();

    public String getWorkingDirectory() {
        return this.runConf.getWorkingDirectory();
    }
}
