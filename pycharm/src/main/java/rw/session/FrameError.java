package rw.session;

import com.intellij.openapi.diagnostic.Logger;
import rw.dialogs.DialogFactory;
import rw.stack.Frame;

public class FrameError extends FrameEvent {
    private static final Logger LOGGER = Logger.getInstance(FrameError.class);

    public static final String ID = "FrameError";
    public static final String VERSION = "0.1.1";

    private int line;

    @Override
    public void handle() {
        LOGGER.info("Handling FrameError " + String.format("(frame=%d, line=%d)", this.getFrameId(), this.getLine()));
        super.handle();
        this.handler.getErrorHighlightManager().clearAll();

        Frame frame = this.handler.getStack().getById(this.getFrameId());
        assert frame != null;

        this.handler.getErrorHighlightManager().add(frame.getPath(), this.line);
        DialogFactory.get().showFirstFrameErrorDialog(this.handler.getProject());
    }

    public int getLine() {
        return line;
    }
}
