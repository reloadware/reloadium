package rw.session.events;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Map;

public class LineProfile extends FileEvent {
    private static final Logger LOGGER = Logger.getInstance(LineProfile.class);

    public static final String ID = "LineProfile";

    private Boolean stop;
    @SerializedName("memory_values")
    private Map<Integer, Long> memoryValues;
    @SerializedName("time_values")
    private Map<Integer, Long> timeValues;
    private Integer line;
    private String frame;
    @SerializedName("frame_line")
    private Integer frameLine;

    @Override
    public void handle() {
        LOGGER.debug("Handling LineProfile");
        super.handle();
        this.handler.getStackRenderer().onFrameProgressEvent(this);
        this.handler.getTimeProfiler().onLineProfileEvent(this);
        this.handler.getMemoryProfiler().onLineProfileEvent(this);
        this.handler.getActiveProfiler().update();
    }

    public Boolean isStop() {
        return this.stop;
    }

    public Map<Integer, Long> getMemoryValues() {
        return this.memoryValues;
    }
    public Map<Integer, Long> getTimeValues() {
        return this.timeValues;
    }

    public Integer getLine() {
        return this.line;
    }
    public String getFrame() {
        return this.frame;
    }
    public Integer getFrameLine() {
        return this.frameLine;
    }
}
