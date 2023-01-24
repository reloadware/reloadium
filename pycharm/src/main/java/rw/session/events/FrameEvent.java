package rw.session.events;

import com.google.gson.annotations.SerializedName;

abstract public class FrameEvent extends FileEvent {
    @SerializedName("frame_id")
    private Long frameId;

    @SerializedName("fullname")
    private String fullname;

    @Override
    public void handle() {
        super.handle();
    }
}
