package rw.session.events;

import com.intellij.openapi.diagnostic.Logger;
import rw.dialogs.DialogFactory;

public class UserError extends FileError {
    private static final Logger LOGGER = Logger.getInstance(UserError.class);

    public static final String ID = "UserError";

    @Override
    public void handle() {
        LOGGER.info("Handling UserError " + String.format("(%s, %d)", this.getLocalPath(), this.getLine()));
        super.handle();

        DialogFactory.get().showFirstUserErrorDialog(this.handler.getProject());
    }
}
