package rw.session.events;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;
import rw.dialogs.DialogFactory;

public class ThreadErrorEvent extends FileError {
    public static final String ID = "ThreadError";
    private static final Logger LOGGER = Logger.getInstance(ThreadErrorEvent.class);
    @SerializedName("thread_id")
    final private String threadId;

    @SerializedName("frame_id")
    final private Long frameId;

    @Override
    public void handle() {
        LOGGER.info("Handling ThreadErrorEvent");
        this.handler.getThreadErrorManager().onThreadError(this);
        DialogFactory.get().showFirstThreadErrorDialog(this.handler.getProject());
    }

    @VisibleForTesting
    public ThreadErrorEvent(@NotNull String path, @NotNull VirtualFile file, Integer line, String msg, String threadId, Long frameId) {
        super(path, file, line, msg);
        this.threadId = threadId;
        this.frameId = frameId;
    }

    public String getThreadId() {
        return this.threadId;
    }

    public Long getFramenId() {
        return this.frameId;
    }
}
