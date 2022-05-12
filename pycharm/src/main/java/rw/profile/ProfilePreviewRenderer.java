package rw.profile;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import rw.frame.FileFrames;
import rw.frame.Frame;
import rw.frame.FrameManager;
import rw.highlights.Highlighter;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfilePreviewRenderer {
    List<Highlighter> highlighters;

    Project project;
    FrameManager frameManager;

    public ProfilePreviewRenderer(Project project, FrameManager frameManager) {
        this.project = project;
        this.frameManager = frameManager;
        this.highlighters = new ArrayList<>();
    }

    public void deactivate() {
        for (Highlighter h : this.highlighters) {
            h.hide();
        }
    }

    public void activate() {
        this.deactivate();
        this.highlighters.clear();

        for (Map.Entry<File, FileFrames> pathToFramesE : this.frameManager.getPathToFrames().entrySet()) {
            for (Map.Entry<Integer, Color> lineToColorE : pathToFramesE.getValue().getLineToColor().entrySet()) {
                Color color = new Color(lineToColorE.getValue().getRed(),
                        lineToColorE.getValue().getGreen(),
                        lineToColorE.getValue().getBlue(),
                        150);

                Highlighter highlighter = new Highlighter(this.project,
                        pathToFramesE.getKey(),
                        lineToColorE.getKey(),
                        color,
                        1, true);

                this.highlighters.add(highlighter);
            }
            for (Highlighter h: this.highlighters) {
                h.show();
            }
        }
    }

    public void update() {
        this.deactivate();
        this.activate();
    }
}