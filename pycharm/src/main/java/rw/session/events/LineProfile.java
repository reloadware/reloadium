package rw.session.events;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Map;

public class LineProfile extends FileEvent {
    public static final String ID = "LineProfile";
    private static final Logger LOGGER = Logger.getInstance(LineProfile.class);
    @SerializedName("memory_values")
    private final Map<Integer, Long> memoryValues;
    @SerializedName("time_values")
    private final Map<Integer, Long> timeValues;

    @SerializedName("frame_uuid")
    private final Long frameUuid;

    @SerializedName("thread_id")
    private final String threadId;

    @Nullable private final Integer pointer;

    @SerializedName("partial")
    private final Boolean partial;

    @VisibleForTesting
    public LineProfile(@NotNull String path,
                       @NotNull VirtualFile file,
                       Map<Integer, Long> memoryValues,
                       Map<Integer, Long> timeValues,
                       @Nullable Integer pointer,
                       @NotNull Long frameUuid,
                       @NotNull Boolean partial,
                       @NotNull String threadId) {
        super(path, file);
        this.partial = partial;
        this.memoryValues = memoryValues;
        this.timeValues = timeValues;
        this.pointer = pointer;
        this.frameUuid = frameUuid;
        this.threadId = threadId;
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

    public Map<Integer, Long> getMemoryValues() {
        return this.memoryValues;
    }

    public Map<Integer, Long> getTimeValues() {
        return this.timeValues;
    }

    @Nullable public Integer getPointer() {
        return this.pointer;
    }

    public String getThreadId() {
        return this.threadId;
    }

    public Long getFrameUuid() {
        return this.frameUuid;
    }

    public Boolean isPartial() {
        return this.partial;
    }
}
