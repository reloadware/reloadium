package rw.session.events;

import com.intellij.openapi.diagnostic.Logger;

public class ClearThreadError extends ThreadEvent {
    private static final Logger LOGGER = Logger.getInstance(ClearThreadError.class);

    public static final String ID = "ClearThreadError";

    @Override
    public void handle() {
        LOGGER.info("Handling ClearThreadError ");
        super.handle();
        this.handler.getThreadErrorManager().onThreadErrorClear(this);
    }
}
