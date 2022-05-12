package rw.highlights;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.xdebugger.ui.DebuggerColors;

import java.awt.*;
import java.io.File;
import java.util.Objects;

public class Blink {
    private final int lineno;
    private final int endLineno;

    private final Color color;
    private final int layer;

    private long expires;

    private final Project project;
    private final File file;
    private final int duration;

    private Highlighter highlighter = null;

    public Blink(Project project, File file, int lineno, int endLineno, Color color, int layer, int duration) {
        this.project = project;
        this.file = file;
        this.lineno = lineno;
        this.endLineno = endLineno;
        this.color = color;
        this.layer = layer;

        this.duration = duration;

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
        this.highlighter = new Highlighter(this.project,
                this.file,
                this.lineno,
                this.endLineno,
                this.color,
                this.layer,
                false);
        this.highlighter.show();
    }

    public void remove() {
        this.highlighter.hide();
    }
}
