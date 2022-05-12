package rw.session;

import com.intellij.openapi.diagnostic.Logger;
import rw.dialogs.DialogFactory;

public class FrameError extends FrameEvent {
    private static final Logger LOGGER = Logger.getInstance(FrameError.class);

    public static final String ID = "FrameError";
    public static final String VERSION = "0.1.0";

    private int line;

    @Override
    public void handle() {
        LOGGER.info("Handling FrameError " + String.format("(path=%s, frame=%d, line=%d)", this.getPath(), this.getFrameId(),
                this.getLine()));
        super.handle();
        this.handler.getErrorHighlightManager().clearAll();
        this.handler.getErrorHighlightManager().add(this.getLocalPath(), this.line);
        DialogFactory.get().showFirstFrameErrorDialog(this.handler.getProject());
    }

    public int getLine() {
        return line;
    }
}
