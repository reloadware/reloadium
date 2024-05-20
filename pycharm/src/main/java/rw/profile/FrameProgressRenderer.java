package rw.profile;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import rw.highlights.Blink;
import rw.highlights.Blinker;
import rw.highlights.Highlighter;
import rw.session.events.LineProfile;

import java.awt.*;
import java.util.*;


public class FrameProgressRenderer {
    private static final Logger LOGGER = Logger.getInstance(FrameProgressRenderer.class);
    Project project;
    static public Color FRAME_PROGRESS_COLOR = new Color(0, 149, 255, 50);
    Color CURR_LINE_COLOR = new Color(0, 149, 255, 80);
    int BLINK_DURATION = 300;
    int POINTER_BLINK_DURATION = 500;
    Map<String, Blink> threadBlinkers;

    public FrameProgressRenderer(Project project) {
        this.project = project;
        this.threadBlinkers = new HashMap<>();
    }

    public void onFrameProgressEvent(LineProfile event) {
        LOGGER.info("Rendering frame progress");

        if (event.getTimeValues().isEmpty()) {
            return;
        }

        Set<Integer> lines = new HashSet<>(event.getTimeValues().keySet());

        int start = Collections.min(lines);
        int end = Collections.max(lines);

        Blink blink = new Blink(this.project, event.getFile(), start, end, FRAME_PROGRESS_COLOR, -2,
                this.BLINK_DURATION);
        Blinker.get().blink(blink);


        if (event.getPointer() != null) {
            Blink pointerBlink = this.threadBlinkers.get(event.getThreadId());

            if (pointerBlink != null && pointerBlink.getBegin() != event.getPointer()) {
                pointerBlink.remove();
                pointerBlink = null;
            }

            if(pointerBlink == null) {
                pointerBlink = new Blink(this.project, event.getFile(), event.getPointer(), event.getPointer(),
                    CURR_LINE_COLOR, -1,
                    POINTER_BLINK_DURATION);

                this.threadBlinkers.put(event.getThreadId(), pointerBlink);
            }

            Blinker.get().blink(pointerBlink);
        }
    }
}
