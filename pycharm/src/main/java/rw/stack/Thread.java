package rw.stack;

import org.jetbrains.annotations.Nullable;

public class Thread {
    String id;

    @Nullable ThreadError threadError;

    public Thread(String id) {
        this.id = id;
        this.threadError = null;
    }
}
