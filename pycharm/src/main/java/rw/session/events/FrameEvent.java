package rw.session.events;

import com.google.gson.annotations.SerializedName;

abstract public class FrameEvent extends Event {
    @SerializedName("frame_id")
    private Long frameId;

    public Long getFrameId() {
        return frameId;
    }

    @Override
    public void handle() {
        super.handle();
    }
}
