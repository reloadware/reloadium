package rw.session;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import rw.handler.runConf.PythonRunConfHandler;

import java.io.File;

public class UpdateModule extends Event {
    private static final Logger LOGGER = Logger.getInstance(UpdateModule.class);

    public static final String ID = "UpdateModule";
    File file;

    UpdateModule(Project project, PythonRunConfHandler handler, String[] args) {
        super(project, handler);
        this.file = new File(this.handler.convertPathToLocal(args[0]));
    }

    @Override
    public void handle() {
        LOGGER.info("Handling UpdateModule " + String.format("(%s)", this.file));

        HighlightManager.get().clearFile(this.file);
    }
}
