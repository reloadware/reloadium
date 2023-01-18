package rw.handler.runConf;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.projectRoots.Sdk;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import com.jetbrains.python.sdk.PythonSdkUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RunConfHandlerFactory {
    public static @NotNull BaseRunConfHandler factory(RunConfiguration runConf) {
        AbstractPythonRunConfiguration pythonRunConf = (AbstractPythonRunConfiguration) runConf;

        Sdk sdk = pythonRunConf.getSdk();
        assert sdk != null;

        if (PythonSdkUtil.isRemote(sdk) && (
                sdk.getHomePath().startsWith("docker-compose://") || sdk.getHomePath().startsWith("docker://"))
        ) {
            return new DockerRunConfHandler(runConf);
        }
        else if (PythonSdkUtil.isRemote(sdk)){
            return new RemoteRunConfHandler(runConf);
        }
        else {
            return new PythonRunConfHandler(runConf);
        }

    }
}
