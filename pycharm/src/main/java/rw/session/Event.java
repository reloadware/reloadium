package rw.session;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.VisibleForTesting;
import rw.handler.runConf.PythonRunConfHandler;

abstract public class Event {
    protected final Project project;

    public static final String ID = null;
    protected PythonRunConfHandler handler;

    protected Event(Project project, PythonRunConfHandler handler) {
        this.project = project;
        this.handler = handler;
    }

    @VisibleForTesting
    public abstract void handle();
}
