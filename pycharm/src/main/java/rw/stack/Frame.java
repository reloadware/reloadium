package rw.stack;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.Nullable;
import rw.highlights.Blink;
import rw.highlights.Blinker;
import rw.highlights.Highlighter;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;
import rw.session.FrameEvent;
import rw.session.FrameProgress;
import rw.util.colormap.ColorMap;
import rw.util.colormap.ColorMaps;

import java.awt.*;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Frame {
    private static final Logger LOGGER = Logger.getInstance(Frame.class);

    final private Long id;
    final private File path;
    final private Integer bodyLineno;
    final private Integer endLineno;
    final private Integer handlerLineno;
    final private String fullname;
    final private Integer number;

    static private Integer counter = 0;

    private Highlighter currLineHighlighter = null;
    MessageBusConnection messageBusConnection;

    Map<Integer, Long> lineTiming;
    Integer prevLine;

    Color PROGRESS_COLOR = new Color(0, 149, 255, 50);

    public Frame(Long id, File path, Integer bodyLineno, Integer endLineno, Integer handlerLineno, String fullname) {
        this.id = id;
        this.path = path;
        this.bodyLineno = bodyLineno;
        this.endLineno = endLineno;
        this.handlerLineno = handlerLineno;
        this.fullname = fullname;

        this.lineTiming = new HashMap<>();

        this.number = Frame.counter;
        Frame.counter += 1;

        this.prevLine = bodyLineno;
    }

    public Long getId() {
        return id;
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public boolean equals(Object other) {
        if (other.getClass() != this.getClass()) {
            return false;
        }

        return ((Frame) other).getId().equals(this.getId());
    }

    public Integer getBodyLineno() {
        return this.bodyLineno;
    }

    public Integer getEndLineno() {
        return this.endLineno;
    }

    public Integer getHandlerLineno() {
        return this.handlerLineno;
    }

    public File getPath() {
        return this.path;
    }

    public Map<Integer, Long> getLineTiming() {
        return new HashMap<>(lineTiming);
    }

    public void updateLineTiming(Map<Integer, Long> lineTiming) {
        this.lineTiming.clear();
        this.lineTiming.putAll(lineTiming);
    }

    public Map<Integer, Color> getLineColors() {
        ColorMap colorMap = ColorMaps.get().getSelected();

        Map<Integer, Color> ret = new HashMap<>();

        Map<Integer, Long> timing = new HashMap<>(this.lineTiming);

        if (timing.isEmpty()) {
            return ret;
        }

        Long min = Collections.min(timing.values());
        Long max = Collections.max(timing.values());

        for (Map.Entry<Integer, Long> entry : timing.entrySet()) {
            Long lineTime = entry.getValue();

            if (lineTime == null) {
                return null;
            }

            Color color = colorMap.getColor(min, max, lineTime);
            ret.put(entry.getKey(), color);
        }
        return ret;
    }

    @Nullable
    public Float getLineTimeMs(int line) {
        Long time = this.getLineTimeNs(line);
        if (time == null) {
            return null;
        }

        float ret = time / 1e6f;
        return ret;
    }

    @Nullable
    public Long getLineTimeNs(int line) {
        Map<Integer, Long> lineTiming = this.lineTiming;

        int cleanLine = line + 1;
        Long ret = lineTiming.get(cleanLine);
        return ret;
    }

    public String getFullname() {
        return fullname;
    }

    public Integer getNumber() {
        return number;
    }

    public void renderProgress(Project project, FrameProgress event) {
        if (this.prevLine.equals(event.getLine())) {
            return;
        }

        if (!event.isStop()) {
            if (this.currLineHighlighter == null) {
                this.currLineHighlighter = new Highlighter(project, this.getPath(), event.getLine(), this.PROGRESS_COLOR,
                        -1, false);
            } else if (this.currLineHighlighter.getLine() != event.getLine()) {
                this.currLineHighlighter.hide();

                this.currLineHighlighter = new Highlighter(project, this.getPath(), event.getLine(), this.PROGRESS_COLOR,
                        -1, false);
            }
            this.currLineHighlighter.show();
        }
        else if (this.currLineHighlighter != null){
            this.currLineHighlighter.hide();
        }

        LOGGER.info("Rendering frame progress" + String.format("(%d, %s, %d)", this.getId(), this.getPath(), event.getLine()));

        PreferencesState state = Preferences.getInstance().getState();

        Blink blink = new Blink(project, this.getPath(), this.prevLine, event.getLine(), this.PROGRESS_COLOR, -1,
                state.blinkDuration);
        Blinker.get().blink(blink);

        this.prevLine = event.getLine();

        Map<Integer, Long> lineTiming = event.getLineTiming();
        lineTiming.remove(this.getHandlerLineno());
    }

    public void activate() {
        if (this.currLineHighlighter != null) {
            this.currLineHighlighter.show();
        }
    }

    public void deactivate() {
        if (this.currLineHighlighter != null) {
            this.currLineHighlighter.hide();
        }
    }
}
