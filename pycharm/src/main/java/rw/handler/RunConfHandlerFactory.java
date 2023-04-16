package rw.handler;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import com.jetbrains.python.sdk.PythonSdkUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class RunConfHandlerFactory {
    public static @NotNull RunConfHandler factory(RunConfiguration runConf) {
        AbstractPythonRunConfiguration pythonRunConf = (AbstractPythonRunConfiguration) runConf;

        Sdk sdk = pythonRunConf.getSdk();
        assert sdk != null;

        SdkAdditionalData additionalData = sdk.getSdkAdditionalData();

        String extraInfo = "";

        if (additionalData != null) {
            try {
                Method _targetEnvironmentConfigurationField = additionalData.getClass().getMethod("getTargetEnvironmentConfiguration");
                _targetEnvironmentConfigurationField.setAccessible(true);
                Object _targetEnvironmentConfiguration = _targetEnvironmentConfigurationField.invoke(additionalData);
                extraInfo = _targetEnvironmentConfiguration.getClass().getName().toLowerCase();
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
            }
        }

        if (PythonSdkUtil.isRemote(sdk) && extraInfo.contains("docker")) {
            return new DockerRunConfHandler(runConf);
        } else if (PythonSdkUtil.isRemote(sdk)) {
            return new SshRunConfHandler(runConf);
        } else {
            return new PythonRunConfHandler(runConf);
        }

    }
}
