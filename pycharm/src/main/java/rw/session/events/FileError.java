package rw.session.events;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

abstract public class FileError extends FileEvent {
    public static final String ID = "FileError";

    final private Integer line;
    final private String msg;

    @Override
    public void handle() {
        this.handler.getErrorHighlightManager().add(this.getFile(), this.line, this.msg);
    }

    @VisibleForTesting
    public FileError(@NotNull String path, @NotNull VirtualFile file, Integer line, String msg) {
        super(path, file);
        this.line = line;
        this.msg = msg;
    }

    public Integer getLine() {
        return line;
    }

    public String getMsg() {
        return msg;
    }
}
