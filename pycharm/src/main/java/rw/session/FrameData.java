package rw.session;

import com.google.gson.annotations.SerializedName;

import java.io.File;

public class FrameData {
    @SerializedName("frame_id")
    private Long frameId;
    @SerializedName("body_lineno")
    private Integer bodyLineno;

    @SerializedName("end_lineno")
    private Integer endLineno;

    private String path;
    transient public String localPath;

    @SerializedName("handler_lineno")
    private Integer handlerLineno;

    private String fullname;

    public Long getFrameId() {
        return frameId;
    }

    public Integer getBodyLineno() {
        return this.bodyLineno;
    }

    public Integer getEndLineno() {
        return this.endLineno;
    }

    public Integer getHandlerLineno() {
        return this.handlerLineno;
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
}
