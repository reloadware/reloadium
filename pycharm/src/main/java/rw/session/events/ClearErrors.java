package rw.session.events;

import com.intellij.openapi.diagnostic.Logger;

public class ClearErrors extends FileEvent {
    private static final Logger LOGGER = Logger.getInstance(ClearErrors.class);

    public static final String ID = "ClearErrors";
    public static final String VERSION = "0.1.0";

    @Override
    public void handle() {
        LOGGER.info("Handling ClearErrors " + String.format("(file=%s)", this.getLocalPath()));
        super.handle();
        this.handler.getErrorHighlightManager().clearFile(this.getLocalPath());
    }
}
