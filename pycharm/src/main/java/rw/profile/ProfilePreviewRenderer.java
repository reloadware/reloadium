package rw.profile;

import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.project.Project;
import rw.stack.Stack;
import rw.highlights.Highlighter;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProfilePreviewRenderer {
    List<Highlighter> highlighters;

    Project project;
    LineProfiler lineProfiler;
    Stack stack;

    public ProfilePreviewRenderer(Project project, Stack stack, LineProfiler lineProfiler) {
        this.project = project;
        this.stack = stack;
        this.lineProfiler = lineProfiler;
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

        for (Map.Entry<File, FileTiming> pathToFileTiming : this.lineProfiler.getFileTimings().entrySet()) {
            File path = pathToFileTiming.getKey();
            FileTiming fileTiming = pathToFileTiming.getValue();

            for (Map.Entry<Integer, Color> lineToColorE : fileTiming.getColors().entrySet()) {
                Integer line = lineToColorE.getKey();
                Color color = lineToColorE.getValue();

                Highlighter highlighter = new Highlighter(this.project, path, line, color,1, true);
                this.highlighters.add(highlighter);
            }

            // Fill empty spaces
            Set<Integer> lines = fileTiming.getColors().keySet();
            if (lines.isEmpty()) {
                continue;
            }
            Integer min = Collections.min(lines);
            Integer max = Collections.max(lines);

            Set<Integer> blanks = IntStream.rangeClosed(min, max).boxed().collect(Collectors.toSet());

            blanks.removeAll(lines);

            for (Integer l: blanks) {
                Highlighter highlighter = new Highlighter(this.project, path, l, emptyColor,1, true);
                this.highlighters.add(highlighter);
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