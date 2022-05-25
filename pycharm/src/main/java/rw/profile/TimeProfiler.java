package rw.profile;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;
import rw.session.FrameData;
import rw.session.FrameProgress;
import rw.session.StackUpdate;
import rw.stack.Frame;
import rw.stack.Stack;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TimeProfiler {
    Stack stack;
    Map<File, Map<Integer, Long>> lineToNs;
    Map<File, Map<Integer, Color>> lineToColor;

    public TimeProfiler(Stack stack) {
        this.stack = stack;
        this.lineToNs = new HashMap<>();
        this.lineToColor = new HashMap<>();
    }

    @Nullable
    public Color getLineColor(File path, int line) {
        Map<Integer, Color> fileLineToColor = this.lineToColor.get(path);

        if (fileLineToColor == null) {
            return null;
        }
        return fileLineToColor.get(line);
    }

    public void onFrameProgressEvent(FrameProgress event) {
        this.lineToNs.clear();
        this.lineToColor.clear();

        for(Frame f: this.stack.getAllFrames()) {
            this.lineToNs.putIfAbsent(f.getPath(), new HashMap<>());
            this.lineToNs.get(f.getPath()).putAll(f.getLineTiming());

            this.lineToColor.putIfAbsent(f.getPath(), new HashMap<>());
            this.lineToColor.get(f.getPath()).putAll(f.getLineColors());
        }
    }

    @Nullable
    public Float getLineTimeMs(File path, int line) {
        Long time = this.getLineTimeNs(path, line);
        if(time == null) {
            return null;
        }

        float ret = time / 1e6f;
        return ret;
    }

    @Nullable public Long getLineTimeNs(File path, int line) {
        Map<Integer, Long> fileLineToTime = this.lineToNs.get(path);

        if (fileLineToTime == null) {
            return null;
        }
        return fileLineToTime.get(line);
    }

    public Map<File, Map<Integer, Color>> getLineToColor() {
        return this.lineToColor;
    }
}
