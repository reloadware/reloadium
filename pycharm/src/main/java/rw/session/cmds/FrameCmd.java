package rw.session.cmds;

import com.google.gson.annotations.SerializedName;

abstract public class FrameCmd extends Cmd {
    @SerializedName("frame_id")
    private String frameId;

    public FrameCmd(String frameId) {
        this.frameId = frameId;
    }
}
