package rw.stack;

import com.intellij.openapi.project.Project;
import rw.handler.Activable;
import rw.highlights.ErrorHighlighter;

import java.io.File;

public class ThreadError implements Activable {
    final private File file;
    String msg;
    int line;
    Project project;
    ErrorHighlighter highlighter;
    String threadId;
    boolean active;

    public ThreadError(Project project, String threadId, File file, String msg, int line) {
        this.project = project;
        this.threadId = threadId;
        this.file = file;
        this.msg = msg;
        this.line = line;
        this.highlighter = new ErrorHighlighter(this.project, file, line, msg);
        this.active = false;
    }

    String getThreadId() {
        return this.threadId;
    }

    boolean isActive() {
        return this.active;
    }

    @Override
    public void activate() {
        this.highlighter.show();
        this.active = true;
    }

    @Override
    public void deactivate() {
        this.highlighter.hide();
        this.active = false;
    }
}
