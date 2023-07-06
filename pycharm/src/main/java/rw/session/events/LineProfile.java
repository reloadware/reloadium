package rw.session.events;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Map;

public class LineProfile extends FileEvent {
    public static final String ID = "LineProfile";
    private static final Logger LOGGER = Logger.getInstance(LineProfile.class);
    private final Boolean stop;
    @SerializedName("memory_values")
    private final Map<Integer, Long> memoryValues;
    @SerializedName("time_values")
    private final Map<Integer, Long> timeValues;
    private final Integer line;
    private final String frame;
    @SerializedName("frame_line")
    private final Integer frameLine;

    @VisibleForTesting
    public LineProfile(@NotNull String path,
                       @NotNull VirtualFile file,
                       Boolean stop,
                       Map<Integer, Long> memoryValues,
                       Map<Integer, Long> timeValues,
                       Integer line,
                       String frame,
                       Integer frameLine) {
        super(path, file);
        this.stop = stop;
        this.memoryValues = memoryValues;
        this.timeValues = timeValues;
        this.line = line;
        this.frame = frame;
        this.frameLine = frameLine;
    }

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
