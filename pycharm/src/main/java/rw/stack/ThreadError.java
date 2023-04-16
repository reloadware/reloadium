package rw.stack;

import com.intellij.openapi.project.Project;
import rw.handler.Activable;
import rw.highlights.ErrorHighlighter;

import java.io.File;

public class ThreadError implements Activable {
    final private File file;
    private String msg;
    private int line;
    private ErrorHighlighter highlighter;
    private String threadId;
    private boolean active;

    public ThreadError(Project project, String threadId, File file, String msg, int line) {
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

    public int getLine() {
        return this.line;
    }

    public String getMsg() {
        return this.msg;
    }
}
