package rw.profile;

import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.project.Project;
import rw.stack.Frame;
import rw.stack.Stack;
import rw.highlights.Highlighter;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

public class ProfilePreviewRenderer {
    List<Highlighter> highlighters;

    Project project;
    TimeProfiler timeProfiler;
    Stack stack;

    public ProfilePreviewRenderer(Project project, Stack stack, TimeProfiler timeProfiler) {
        this.project = project;
        this.stack = stack;
        this.timeProfiler = timeProfiler;
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

        Color emptyColor = EditorColorsManager.getInstance().getGlobalScheme().getColor(EditorColors.GUTTER_BACKGROUND);

        for (Map.Entry<File, Map<Integer, Color>> pathToLineColorsE : this.timeProfiler.getLineToColor().entrySet()) {
            File path = pathToLineColorsE.getKey();
            for (Map.Entry<Integer, Color> lineToColorE : pathToLineColorsE.getValue().entrySet()) {
                Integer line = lineToColorE.getKey();
                Color color = lineToColorE.getValue();

                Highlighter highlighter = new Highlighter(this.project, path, line, color,1, true);
                this.highlighters.add(highlighter);
            }

            // Fill empty spaces
            Set<Integer> lines = pathToLineColorsE.getValue().keySet();
            for (Frame f : this.stack.getForPath(path)) {
                if (lines.isEmpty()) {
                    continue;
                }
                IntStream.range(f.getBodyLineno(), f.getEndLineno()+1).forEachOrdered(n -> {
                    if (!lines.contains(n)) {
                        Highlighter highlighter = new Highlighter(this.project, path, n, emptyColor,1, true);

                        this.highlighters.add(highlighter);
                    }
                });
            }
        }
        for (Highlighter h : this.highlighters) {
            h.show();
        }
    }

    public void update() {
        this.deactivate();
        this.activate();
    }
}