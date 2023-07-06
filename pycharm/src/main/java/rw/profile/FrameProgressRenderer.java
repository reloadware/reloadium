package rw.profile;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import rw.highlights.Blink;
import rw.highlights.Blinker;
import rw.highlights.Highlighter;
import rw.session.events.LineProfile;

import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class FrameProgressRenderer {
    private static final Logger LOGGER = Logger.getInstance(FrameProgressRenderer.class);
    Project project;
    Color FRAME_PROGRESS_COLOR = new Color(0, 149, 255, 50);
    Color CURR_LINE_COLOR = new Color(0, 149, 255, 80);
    int BLINK_DURATION = 300;
    private Highlighter highlighter;  // file to current line highlighter

    public FrameProgressRenderer(Project project) {
        this.project = project;
    }

    public void onFrameProgressEvent(LineProfile event) {
        this.renderFrameProgress(event);
    }

    public void renderFrameProgress(LineProfile event) {
        if (this.highlighter != null) {
            ApplicationManager.getApplication().invokeLater(this.highlighter::hide);
        }

        if (!event.isStop()) {
            this.highlighter = new Highlighter(project,
                    event.getFile(),
                    event.getLine(),
                    this.CURR_LINE_COLOR,
                    -1,
                    false);
            ApplicationManager.getApplication().invokeLater(this.highlighter::show);
        }

        LOGGER.info("Rendering frame progress");

        if (!event.getTimeValues().isEmpty()) {
            Set<Integer> lines = new HashSet<>(event.getTimeValues().keySet());
            lines.add(event.getLine());
            int start = Collections.min(lines);
            int end = Collections.max(lines);
            Blink blink = new Blink(project, event.getFile(), start, end, FRAME_PROGRESS_COLOR, -1,
                    this.BLINK_DURATION);
            Blinker.get().blink(blink);
        }
    }

    public void activate() {
        if (this.highlighter == null) {
            return;
        }
        ApplicationManager.getApplication().invokeLater(this.highlighter::show);
    }

    public void deactivate() {
        if (this.highlighter == null) {
            return;
        }
        ApplicationManager.getApplication().invokeLater(this.highlighter::hide);
    }
}
