package rw.profile;

import org.jetbrains.annotations.Nullable;
import rw.session.events.LineProfile;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class LineProfiler {
    Map<File, FileValues> fileTimings;

    public LineProfiler() {
        this.fileTimings = new HashMap<>();
    }

    public void onLineProfileEvent(LineProfile event) {
        this.fileTimings.putIfAbsent(event.getLocalPath(), new FileValues());

        FileValues fileValues = this.fileTimings.get(event.getLocalPath());
        fileValues.update(event.getValues(), event.getDisplay());
    }

    public Map<File, FileValues> getFileTimings() {
        return fileTimings;
    }

    @Nullable
    public Color getLineColor(File path, int line) {
        FileValues fileValues = this.fileTimings.get(path);
        if (fileValues == null) {
            return null;
        }

        return fileValues.getLineColor(line);
    }

    @Nullable
    public String getLine(File path, int line) {
        FileValues fileValues = this.fileTimings.get(path);
        if (fileValues == null) {
            return null;
        }

        return fileValues.getLine(line);
    }

    public void clearFile(File path) {
        FileValues fileValues = this.fileTimings.get(path);
        if (fileValues == null) {
            return;
        }
        fileValues.clear();
    }

    public void clearLines(File path, int start, int end) {
        FileValues fileValues = this.fileTimings.get(path);
        if (fileValues == null) {
            return;
        }
        fileValues.clearLines(start, end);
    }
}
