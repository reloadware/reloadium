package rw.stack;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class Frame {
    private static final Logger LOGGER = Logger.getInstance(Frame.class);
    static private Integer counter = 0;
    final private Long id;
    final private File path;
    final private boolean reloadable;
    @Nullable
    private Frame back;

    @Nullable
    private Frame forward;

    public Frame(Long id, File path, boolean reloadable) {
        this.id = id;
        this.path = path;
        this.reloadable = reloadable;
    }

    public void setForwardFrame(@Nullable Frame forward) {
        this.forward = forward;
    }

    public void setBackFrame(@Nullable Frame back) {
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

    public File getPath() {
        return this.path;
    }


    @Nullable
    public Frame getBack() {
        return this.back;
    }

    @Nullable
    public Frame getForward() {
        return this.forward;
    }

    public boolean isReloadable() {
        return this.reloadable;
    }

    public boolean isDroppable() {
        if (!this.isReloadable()) {
            return false;
        }

        if (this.back == null) {
            return false;
        }

        if (!this.back.isReloadable()) {
            return false;
        }

        Frame current = this.forward;
        while(current != null) {
            if (!current.isReloadable()) {
                return false;
            }
            current = current.forward;
        }

        return true;
    }
}
