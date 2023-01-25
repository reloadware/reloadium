package rw.stack;

import java.io.File;

public class ThreadError {
    Frame frame;
    String msg;
    int line;

    public ThreadError(Frame frame, String msg, int line) {
        this.frame = frame;
        this.msg = msg;
        this.line = line;
    }

    public File getPath() {
        return this.frame.getPath();
    }

    public String getMsg() {
        return this.msg;
    }

    public int getLine() {
        return this.line;
    }
}
