package rw.profile;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import rw.highlights.Blink;
import rw.highlights.Blinker;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;
import rw.session.events.LineProfile;
import rw.highlights.Highlighter;

import java.awt.*;
import java.util.*;


public class FrameProgressRenderer {
    private static final Logger LOGGER = Logger.getInstance(FrameProgressRenderer.class);
    private Highlighter highlighter;  // file to current line highlighter

    Project project;

    Color FRAME_PROGRESS_COLOR = new Color(0, 149, 255, 50);
    Color CURR_LINE_COLOR = new Color(0, 149, 255, 80);
    int BLINK_DURATION = 300;

    public FrameProgressRenderer(Project project) {
        this.project = project;
    }

    public void onFrameProgressEvent(LineProfile event) {
        this.renderFrameProgress(event);
    }

    public void renderFrameProgress(LineProfile event) {
        if (this.highlighter != null) {
            highlighter.hide();
        }

        int start = Collections.min(event.getValues().keySet());

        if (!event.isStop()) {
            this.highlighter = new Highlighter(project,
                            event.getLocalPath(),
                            event.getLine(),
                            this.CURR_LINE_COLOR,
                            -1,
                            false);
            this.highlighter.show();
        }

        LOGGER.info("Rendering frame progress");

        PreferencesState state = Preferences.getInstance().getState();

        Blink blink = new Blink(project, event.getLocalPath(), start, event.getLine(), FRAME_PROGRESS_COLOR, -1,
                this.BLINK_DURATION);
        Blinker.get().blink(blink);
    }


    public void activate() {
        if (this.highlighter == null) {
            return;
        }
        this.highlighter.show();
    }

    public void deactivate() {
        if (this.highlighter == null) {
            return;
        }
        this.highlighter.hide();
    }
}
