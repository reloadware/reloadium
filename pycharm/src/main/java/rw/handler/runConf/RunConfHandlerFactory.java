package rw.handler.runConf;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.projectRoots.Sdk;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import com.jetbrains.python.sdk.PythonSdkUtil;
import org.jetbrains.annotations.Nullable;

public abstract class RunConfHandlerFactory {
    public static @Nullable BaseRunConfHandler factory(RunConfiguration runConf) {
        if (!AbstractPythonRunConfiguration.class.isAssignableFrom(runConf.getClass())) {
            return null;
        }

        AbstractPythonRunConfiguration pythonRunConf = (AbstractPythonRunConfiguration) runConf;

        Sdk sdk = pythonRunConf.getSdk();

        if (sdk == null) {
            return null;
        }

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
