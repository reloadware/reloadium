package rw.session;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.diagnostic.Logger;

import java.util.Map;

public class FrameProgress extends FrameEvent {
    private static final Logger LOGGER = Logger.getInstance(FrameProgress.class);

    public static final String ID = "FrameProgress";
    public static final String VERSION = "0.1.1";

    private Integer line;
    private Boolean stop;
    @SerializedName("line_timing")
    private Map<Integer, Long> lineTiming;

    @Override
    public void handle() {
        LOGGER.debug("Handling FrameProgress " + String.format("frame=%d,  line=%d)", this.getFrameId(), this.getLine()));
        super.handle();
        this.handler.getStack().onFrameProgressEvent(this);
        this.handler.getTimeProfiler().onFrameProgressEvent(this);
        this.handler.getProfilePreviewRenderer().update();
    }

    public Integer getLine() {
        return this.line;
    }
    public Boolean isStop() {
        return this.stop;
    }

    public Map<Integer, Long> getLineTiming() {
        return this.lineTiming;
    }
}
