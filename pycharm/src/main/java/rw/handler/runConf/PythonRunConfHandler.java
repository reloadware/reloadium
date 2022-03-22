package rw.handler.runConf;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.application.ApplicationInfo;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import rw.action.RunType;
import rw.config.Config;
import rw.handler.sdk.BaseSdkHandler;
import rw.handler.sdk.SdkHandlerFactory;


public class PythonRunConfHandler extends BaseRunConfHandler {
    AbstractPythonRunConfiguration<?> runConf;
    AbstractPythonRunConfiguration<?> origRunConf;

    public PythonRunConfHandler(RunConfiguration runConf) {
        super(runConf);
        this.runConf = (AbstractPythonRunConfiguration<?>) runConf;
        this.origRunConf = (AbstractPythonRunConfiguration<?>) runConf.clone();
    }

    @Override
    public void beforeRun(RunType runType) {
        String command;

        if (runType==RunType.DEBUG) {
            command = "pydev_proxy";
        }
        else {
            command = "run";
        }

        this.runConf.setInterpreterOptions(String.format("-m %s %s", Config.get().packageName, command));

        this.runConf.getEnvs().put(Config.get().ideNameEnvVar, ApplicationInfo.getInstance().getFullApplicationName());
        this.runConf.getEnvs().put(Config.get().idePluginVersionEnvVar, Config.get().version);
        this.runConf.getEnvs().put(Config.get().ideVersionEnvVar, ApplicationInfo.getInstance().getFullVersion());
        this.runConf.getEnvs().put(Config.get().ideNameEnvVar, ApplicationInfo.getInstance().getFullApplicationName());

        // Set PYTHONPATH
        String pythonpath = this.runConf.getEnvs().getOrDefault("PYTHONPATH", "");
        String separator;

        separator = System.getProperty("path.separator");

        String packagePath = this.sdkHandler.getPackageDir().toString();

        if(!pythonpath.isBlank()) {
            pythonpath = String.format("%s%s%s", packagePath, separator, pythonpath);
        }
        else
            pythonpath = packagePath;

        this.runConf.getEnvs().put("PYTHONPATH", pythonpath);
    }

    @Override
    public void afterRun() {
        this.runConf.setInterpreterOptions(this.origRunConf.getInterpreterOptions());
        runConf.setEnvs(this.origRunConf.getEnvs());
    }

    public boolean isActivated() {
        boolean ret = this.runConf.getInterpreterOptions().contains("reloadium");
        return ret;
    }

    @Override
    public boolean canRun() {
        if (this.sdkHandler == null) {
            return false;
        }

        return this.sdkHandler.isValid();
    }
}
