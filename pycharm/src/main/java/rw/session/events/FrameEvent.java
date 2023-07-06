package rw.session.events;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

abstract public class FrameEvent extends FileEvent {
    @SerializedName("frame_id")
    final private Long frameId;

    @SerializedName("fullname")
    final private String fullname;

    @VisibleForTesting
    public FrameEvent(@NotNull String path,
                      @NotNull VirtualFile file,
                      Long frameId,
                      String fullname) {
        super(path, file);
        this.frameId = frameId;
        this.fullname = fullname;
    }

    @Override
    public void handle() {
        super.handle();
    }

    public Long getFrameId() {
        return this.frameId;
    }

    public String getFullname() {
        return this.fullname;
    }
}
