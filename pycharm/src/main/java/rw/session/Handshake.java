package rw.session;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import rw.handler.runConf.BaseRunConfHandler;
import rw.handler.runConf.PythonRunConfHandler;

import java.util.UUID;


public class Handshake extends Event {
    private static final Logger LOGGER = Logger.getInstance(Handshake.class);

    public static final String ID = "Handshake";
    public static final String VERSION = "0.1.0";

    private String version;

    @Override
    public void handle() {
        LOGGER.info("Handshake from the client, version = " + this.version);
    }

    public String getVersion() {
        return version;
    }
}
