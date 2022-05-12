package rw.frame;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.Nullable;
import rw.session.FrameProgress;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class FileFrames {
    final private File path;
    Map<String, Frame> frameFullnameToFrame;
    Map<Integer, Long> lineToNs;
    Map<Integer, Color> lineToColor;

    public FileFrames(File path) {
        this.path = path;
        this.frameFullnameToFrame = new HashMap<>();
        this.lineToNs = new HashMap<>();
        this.lineToColor = new HashMap<>();
    }

    @Nullable
    public Color getLineColor(int line) {
        return this.lineToColor.get(line);
    }

    @Nullable
    public Frame getByFullname(String fullname) {
        return this.frameFullnameToFrame.get(fullname);
    }

    public void updateLineTiming() {
        this.lineToNs.clear();
        this.lineToColor.clear();

        for(Frame f: this.getSortedFrames()) {
            this.lineToNs.putAll(f.getLineTiming());
            this.lineToColor.putAll(f.getLineColors());
        }
    }

    public void onFrameProgressEvent(Project project, FrameProgress event) {
        Frame frame = this.frameFullnameToFrame.get(event.getFullname());
        if (frame == null) {
            frame = Frame.fromEventFactory(event);
            this.frameFullnameToFrame.put(event.getFullname(), frame);
            return;
        }
        frame.updateLineTiming(event.getLineTiming());
        frame.renderProgress(project, event);
        this.updateLineTiming();
    }

    @Nullable
    public Float getLineTimeMs(int line) {
        Long time = this.getLineTimeNs(line);
        if(time == null) {
            return null;
        }

        float ret = time / 1e6f;
        return ret;
    }

    @Nullable public Long getLineTimeNs(int line) {
        return this.lineToNs.get(line);
    }

    private List<Frame> getSortedFrames() {
        List<Frame> frames = new ArrayList<Frame>(this.frameFullnameToFrame.values());
        frames.sort(Comparator.comparing(Frame::getNumber));
        return frames;
    }

    public Map<Integer, Color> getLineToColor() {
        return lineToColor;
    }
}
