package rw.highlights;

import com.google.api.VisibilityRule;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;
import rw.handler.Activable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ErrorHighlightManager implements Activable {
    Map<VirtualFile, List<ErrorHighlighter>> all;

    Project project;

    @VisibleForTesting
    public ErrorHighlightManager(Project project) {
        this.all = new HashMap<>();
        this.project = project;
    }

    synchronized public void add(@NotNull VirtualFile file, int line, String msg) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (!this.all.containsKey(file)) {
                this.all.put(file, new ArrayList<>());
            }

            if (line < 0) {
                return;
            }

            ErrorHighlighter highlighter = new ErrorHighlighter(this.project, file, line, msg);
            this.all.get(file).add(highlighter);

            highlighter.show();
        });
    }

    synchronized public void clearFile(@NotNull VirtualFile file) {
        ApplicationManager.getApplication().invokeLater(() -> {
            List<ErrorHighlighter> highlighters = this.all.get(file);
            if (highlighters == null) {
                return;
            }

            for (ErrorHighlighter h : highlighters) {
                h.hide();
            }
            highlighters.clear();
        });
    }

    synchronized public void clearAll() {
        for (VirtualFile f : this.all.keySet()) {
            this.clearFile(f);
        }
        this.all.clear();
    }

    synchronized public void activate() {
        ApplicationManager.getApplication().invokeLater(() -> {
            for (List<ErrorHighlighter> hs : this.all.values()) {
                for (ErrorHighlighter h : hs) {
                    h.show();
                }
            }
        });
    }

    synchronized public void deactivate() {
        ApplicationManager.getApplication().invokeLater(() -> {
            for (List<ErrorHighlighter> hs : this.all.values()) {
                for (ErrorHighlighter h : hs) {
                    h.hide();
                }
            }
        });
    }
}
