package rw.session;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import rw.handler.runConf.PythonRunConfHandler;


public class Handshake extends Event {
    private static final Logger LOGGER = Logger.getInstance(Handshake.class);

    public static final String ID = "Handshake";

    private final String version;

    Handshake(Project project, PythonRunConfHandler handler, String[] args) {
        super(project, handler);
        this.version = args[0];
    }

    @Override
    public void handle() {
        LOGGER.info("Handshake from the client, version = " + this.version);
    }
}
