package rw.stack;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class Thread {
    String id;

    @Nullable ThreadError threadError;

    public Thread(String id) {
        this.id = id;
        this.threadError = null;
    }
}
