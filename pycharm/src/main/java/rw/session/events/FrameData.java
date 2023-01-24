package rw.session.events;

import com.google.gson.annotations.SerializedName;

import java.io.File;

public class FrameData {
    @SerializedName("frame_id")
    private Long frameId;

    @SerializedName("reloadable")
    private boolean reloadable;

    private String path;
    transient public String localPath;

    private String fullname;

    public Long getFrameId() {
        return frameId;
    }

    public String getFullname() {
        return this.fullname;
    }

    public File getPath() {
        return new File(this.path);
    }

    public File getLocalPath() {
        return new File(this.localPath);
    }

    public boolean isReloadable() {
        return this.reloadable;
    }
}
