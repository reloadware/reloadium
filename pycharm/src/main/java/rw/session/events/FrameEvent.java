package rw.session.events;

import com.google.gson.annotations.SerializedName;

abstract public class FrameEvent extends FileEvent {
    @SerializedName("frame_id")
    private Long frameId;

    @SerializedName("lineno")
    private Integer lineno;

    @SerializedName("handler_lineno")
    private Integer handlerLineno;

    @SerializedName("fullname")
    private String fullname;

    public Long getFrameId() {
        return this.frameId;
    }

    public Integer getLineno() {
        return this.lineno;
    }

    public Integer getHandlerLineno() {
        return this.handlerLineno;
    }

    @Override
    public void handle() {
        super.handle();
    }
}
