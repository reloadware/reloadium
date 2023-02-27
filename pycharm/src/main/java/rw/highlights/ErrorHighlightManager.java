package rw.highlights;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.VisibleForTesting;
import rw.handler.Activable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ErrorHighlightManager implements Activable {
    Map<File, List<ErrorHighlighter>> all;

    Project project;

    @VisibleForTesting
    public ErrorHighlightManager(Project project) {
        this.all = new HashMap<>();
        this.project = project;
    }

    public void add(File file, int line, String msg) {
        if (!this.all.containsKey(file)) {
            this.all.put(file, new ArrayList<>());
        }

        if (line < 0){
            return;
        }

        ErrorHighlighter highlighter = new ErrorHighlighter(this.project, file, line, msg);
        this.all.get(file).add(highlighter);

        highlighter.show();
    }

    public void clearFile(File file) {
        List<ErrorHighlighter> highlighters = this.all.get(file);
        if (highlighters == null) {
            return;
        }

        for (ErrorHighlighter h : highlighters) {
            h.hide();
        }
        highlighters.clear();
    }

    public void clearAll() {
        for (File f : this.all.keySet()) {
            this.clearFile(f);
        }
        this.all.clear();
    }

    public void activate() {
        for (List<ErrorHighlighter> hs : this.all.values()) {
            for (ErrorHighlighter h : hs) {
                h.show();
            }
        }
    }

    public void deactivate() {
        for (List<ErrorHighlighter> hs : this.all.values()) {
            for (ErrorHighlighter h : hs) {
                h.hide();
            }
        }
    }
}
