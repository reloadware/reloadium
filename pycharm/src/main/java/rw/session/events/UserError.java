package rw.session.events;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;
import rw.dialogs.DialogFactory;

public class UserError extends FileError {
    public static final String ID = "UserError";
    private static final Logger LOGGER = Logger.getInstance(UserError.class);

    @VisibleForTesting
    public UserError(@NotNull String path,
                     @NotNull VirtualFile file,
                     Integer line,
                     String msg) {
        super(path, file, line, msg);
    }

    @Override
    public void handle() {
        LOGGER.info("Handling UserError " + String.format("(%s, %d)", this.getFile(), this.getLine()));
        super.handle();

        DialogFactory.get().showFirstUserErrorDialog(this.handler.getProject());
    }
}
