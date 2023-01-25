package rw.stack;

import org.jetbrains.annotations.Nullable;

import java.io.File;

public class Thread {
    String id;

    @Nullable ThreadError threadError;

    public Thread(String id) {
        this.id = id;
        this.threadError = null;
    }

    public void makeErrored(Frame frame, String message, int line) {
        this.threadError = new ThreadError(frame, message, line);
    }

    public void clearErrored() {
        this.threadError = null;
    }

    @Nullable ThreadError getThreadError() {
        return this.threadError;
    }

    boolean isErrored() {
        return this.threadError == null;
    }
}
