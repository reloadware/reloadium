package rw.handler;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.ui.RunContentDescriptor;
import rw.action.RunType;


public class DockerRunConfHandler extends RemoteRunConfHandler {
    public static final String DOCKER_ENV = "RW_DOCKER";  //  # RwRender: public static final String DOCKER_ENV = "{{ ctx.env_vars.ide.docker }}";  //

    public DockerRunConfHandler(RunConfiguration runConf) {
        super(runConf);
    }

    public void onProcessStarted(RunContentDescriptor descriptor) {
    }

    @Override
    public void beforeRun(RunType runType) {
        super.beforeRun(runType);
        this.runConf.getEnvs().put(this.DOCKER_ENV,  "True");
    }
}
