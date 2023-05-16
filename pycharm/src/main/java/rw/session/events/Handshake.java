package rw.session.events;

import com.intellij.openapi.diagnostic.Logger;


public class Handshake extends Event {
    public static final String ID = "Handshake";
    private static final Logger LOGGER = Logger.getInstance(Handshake.class);
    private String version;

    @Override
    public void handle() {
        LOGGER.info("Handshake from the client, version = " + this.version);
    }

    public String getVersion() {
        return version;
    }
}
