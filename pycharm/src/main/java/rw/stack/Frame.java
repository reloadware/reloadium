package rw.stack;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class Frame {
    private static final Logger LOGGER = Logger.getInstance(Frame.class);

    final private Long id;
    final private File path;
    final private String fullname;
    final private boolean reloadable;
    @Nullable final private Frame back;

    static private Integer counter = 0;

    public Frame(Long id, File path, String fullname, boolean reloadable, @Nullable Frame back) {
        this.id = id;
        this.path = path;
        this.fullname = fullname;
        this.back = back;
        this.reloadable = reloadable;
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

    public String getFullname() {
        return this.fullname;
    }

    @Nullable
    public Frame getBack() {
        return this.back;
    }

    public boolean isReloadable() {
        return this.reloadable;
    }
}
