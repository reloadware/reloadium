package rw.session.events;

import com.intellij.openapi.diagnostic.Logger;
import rw.dialogs.DialogFactory;

public class FrameError extends FileError {
    private static final Logger LOGGER = Logger.getInstance(FrameError.class);

    public static final String ID = "FrameError";
    public static final String VERSION = "0.1.2";

    @Override
    public void handle() {
        LOGGER.info("Handling UserError " + String.format("(%s, %d)", this.getLocalPath(), this.getLine()));
        super.handle();
        DialogFactory.get().showFirstFrameErrorDialog(this.handler.getProject());
    }
}
