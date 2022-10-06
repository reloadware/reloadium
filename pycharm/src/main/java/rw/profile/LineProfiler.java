package rw.profile;

import org.jetbrains.annotations.Nullable;
import rw.session.events.LineProfile;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class LineProfiler {
    Map<File, FileTiming> fileTimings;

    public LineProfiler() {
        this.fileTimings = new HashMap<>();
    }

    public void onLineProfileEvent(LineProfile event) {
        this.fileTimings.putIfAbsent(event.getLocalPath(), new FileTiming());

        FileTiming fileTiming = this.fileTimings.get(event.getLocalPath());
        fileTiming.update(event.getTiming());
    }

    public Map<File, FileTiming> getFileTimings() {
        return fileTimings;
    }

    @Nullable
    public Color getLineColor(File path, int line) {
        FileTiming fileTiming = this.fileTimings.get(path);
        if (fileTiming == null) {
            return null;
        }

        return fileTiming.getLineColor(line);
    }

    @Nullable
    public Float getLineTimeMs(File path, int line) {
        FileTiming fileTiming = this.fileTimings.get(path);
        if (fileTiming == null) {
            return null;
        }

        return fileTiming.getLineTimeMs(line);
    }

    @Nullable public Long getLineTimeNs(File path, int line) {
        FileTiming fileTiming = this.fileTimings.get(path);
        if (fileTiming == null) {
            return null;
        }

        return fileTiming.getLineTimeNs(line);
    }

    public void clearFile(File path) {
        FileTiming fileTiming = this.fileTimings.get(path);
        if (fileTiming == null) {
            return;
        }
        fileTiming.clear();
    }

    public void clearLines(File path, int start, int end) {
        FileTiming fileTiming = this.fileTimings.get(path);
        if (fileTiming == null) {
            return;
        }
        fileTiming.clearLines(start, end);
    }
}
