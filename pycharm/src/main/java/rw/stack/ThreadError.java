package rw.stack;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import rw.handler.Activable;
import rw.highlights.ErrorHighlighter;

import java.io.File;

public class ThreadError implements Activable {
    final private VirtualFile file;
    private String msg;
    private int line;
    private ErrorHighlighter highlighter;
    private String threadId;
    private boolean active;

    public ThreadError(Project project, String threadId, @NotNull VirtualFile file, String msg, int line) {
        this.threadId = threadId;
        this.file = file;
        this.msg = msg;
        this.line = line;
        this.highlighter = new ErrorHighlighter(project, file, line, msg);
        this.active = false;
    }

    String getThreadId() {
        return this.threadId;
    }

    public boolean isActive() {
        return this.active;
    }

    @Override
    public void activate() {
        ApplicationManager.getApplication().invokeLater(() -> this.highlighter.show());
        this.active = true;
    }

    @Override
    public void deactivate() {
        ApplicationManager.getApplication().invokeLater(() -> this.highlighter.hide());
        this.active = false;
    }

    public int getLine() {
        return this.line;
    }

    public String getMsg() {
        return this.msg;
    }
}
