package rw.session;

import com.intellij.openapi.project.Project;
import rw.handler.runConf.PythonRunConfHandler;

public class UpdateFrame extends FileEvent {
    public static final String ID = "UpdateFrame";

    public UpdateFrame(Project project, PythonRunConfHandler handler, String[] args) {
        super(project, handler, args);
    }

    @Override
    public void handle() {
    }
}
