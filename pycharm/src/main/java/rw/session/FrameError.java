package rw.session;

import com.intellij.openapi.diagnostic.Logger;
import rw.dialogs.DialogFactory;
import rw.stack.Frame;

public class FrameError extends UserError {
    private static final Logger LOGGER = Logger.getInstance(FrameError.class);

    public static final String ID = "FrameError";
    public static final String VERSION = "0.1.2";

    @Override
    public void handle() {
        LOGGER.info("Handling FrameError");
        super.handle();
        DialogFactory.get().showFirstFrameErrorDialog(this.handler.getProject());
    }
}
