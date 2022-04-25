package rw.handler.runConf;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.ui.RunContentDescriptor;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.action.RunType;
import rw.handler.sdk.BaseSdkHandler;
import rw.handler.sdk.SdkHandlerFactory;
import rw.session.HighlightManager;

import java.util.Map;

public abstract class BaseRunConfHandler {
    AbstractPythonRunConfiguration<?> runConf;
    @Nullable BaseSdkHandler sdkHandler;

    public BaseRunConfHandler(RunConfiguration runConf) {
        this.runConf = (AbstractPythonRunConfiguration<?>)runConf;

        this.sdkHandler = SdkHandlerFactory.factory(this.runConf.getSdk());
    }

    abstract public void beforeRun(RunType runType);
    abstract public void onProcessStarted(RunContentDescriptor descriptor);
    abstract public void afterRun();
    abstract public boolean isActivated();
    public @NotNull String convertPathToLocal(@NotNull String remotePath) {
        return remotePath;
    }
    public @NotNull String convertPathToRemote(@NotNull String localPath) {
        return localPath;
    }

    abstract public boolean canRun();

    public String getWorkingDirectory() {
        return this.runConf.getWorkingDirectory();
    }

    public void onProcessExit() {
        HighlightManager.get().clearAll();
    }
}
