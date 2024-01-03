package rw.session.events;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.File;

abstract public class FileEvent extends Event {
    private static final Logger LOGGER = Logger.getInstance(FileEvent.class);

    public static final String ID = "FileEvent";
    protected String path;

    private VirtualFile file;

    @VisibleForTesting
    public FileEvent(@NotNull String path, @NotNull VirtualFile file) {
        this.path = path;
        this.file = file;
    }

    public boolean isValid() {
        String file = this.handler.convertPathToLocal(this.path, true);
        this.file = new VirtualFileWrapper(new File(file)).getVirtualFile();

        if(this.file == null) {
            LOGGER.warn("Could not get VirtualFile for path %s".formatted(this.path));
            return false;
        }

        return true;
    }

    public VirtualFile getFile() {
        return this.file;
    }
}
