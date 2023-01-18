package rw.stack;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class Frame {
    private static final Logger LOGGER = Logger.getInstance(Frame.class);

    final private Long id;
    final private Long threadId;
    final private File path;
    final private Integer bodyLineno;
    final private Integer endLineno;
    final private Integer handlerLineno;
    final private String fullname;
    @Nullable final private Frame back;

    static private Integer counter = 0;

    public Frame(Long id, Long threadId, File path, Integer bodyLineno, Integer endLineno, Integer handlerLineno, String fullname, @Nullable Frame back) {
        this.id = id;
        this.threadId = threadId;
        this.path = path;
        this.bodyLineno = bodyLineno;
        this.endLineno = endLineno;
        this.handlerLineno = handlerLineno;
        this.fullname = fullname;
        this.back = back;
    }

    public Long getId() {
        return id;
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public boolean equals(Object other) {
        if (other.getClass() != this.getClass()) {
            return false;
        }

        return ((Frame) other).getId().equals(this.getId());
    }

    public Integer getBodyLineno() {
        return this.bodyLineno;
    }

    public Long getThreadId() {
        return this.threadId;
    }

    public Integer getEndLineno() {
        return this.endLineno;
    }

    public Integer getHandlerLineno() {
        return this.handlerLineno;
    }

    public File getPath() {
        return this.path;
    }

    public String getFullname() {
        return this.fullname;
    }

    @Nullable
    public Frame getBack() {
        return this.back;
    }
}
