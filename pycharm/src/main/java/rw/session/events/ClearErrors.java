package rw.session.events;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

public class ClearErrors extends FileEvent {
    public static final String ID = "ClearErrors";
    private static final Logger LOGGER = Logger.getInstance(ClearErrors.class);

    @VisibleForTesting
    public ClearErrors(@NotNull String path,
                       @NotNull VirtualFile file) {
        super(path, file);
    }

    @Override
    public void handle() {
        LOGGER.info("Handling ClearErrors " + String.format("(file=%s)", this.getFile()));
        super.handle();

        this.handler.getErrorHighlightManager().clearFile(this.getFile());
    }
}
