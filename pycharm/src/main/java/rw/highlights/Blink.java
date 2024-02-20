package rw.highlights;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.awt.*;
import java.io.File;

public class Blink {
    private final int lineno;
    private final int endLineno;

    private final Color color;
    private final int layer;
    private final Project project;
    private final VirtualFile file;
    private final int duration;
    private long expires;
    private Highlighter highlighter;

    public Blink(Project project, VirtualFile file, int lineno, int endLineno, Color color, int layer, int duration) {
        this.project = project;
        this.file = file;
        this.lineno = lineno;
        this.endLineno = endLineno;
        this.color = color;
        this.layer = layer;

        this.duration = duration;

        this.highlighter = new Highlighter(this.project,
                this.file,
                this.lineno,
                this.endLineno,
                this.color,
                this.layer,
                false);

        this.resetExpiration();
    }

    public int getBegin() {
        return lineno;
    }

    public int getEndLineno() {
        return endLineno;
    }

    public Color getColor() {
        return this.color;
    }

    public int getLayer() {
        return layer;
    }

    public boolean isExpired() {
        boolean ret = System.currentTimeMillis() >= this.expires;
        return ret;
    }

    public void resetExpiration() {
        this.expires = System.currentTimeMillis() + this.duration;
    }

    public void render() {
        ApplicationManager.getApplication().invokeLater(() -> {
            this.highlighter.show();
        });
    }

    public void remove() {
        ApplicationManager.getApplication().invokeLater(() -> {
            this.highlighter.hide();
        });
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}
