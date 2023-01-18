package rw.session.events;

import com.intellij.openapi.diagnostic.Logger;

public class ClearErrors extends FileEvent {
    private static final Logger LOGGER = Logger.getInstance(ClearErrors.class);

    public static final String ID = "ClearErrors";

    @Override
    public void handle() {
        LOGGER.info("Handling ClearErrors " + String.format("(file=%s)", this.getLocalPath()));
        super.handle();
        this.handler.getErrorHighlightManager().clearFile(this.getLocalPath());
    }
}
