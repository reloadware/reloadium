package rw.session;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.VisibleForTesting;
import rw.handler.runConf.BaseRunConfHandler;
import rw.handler.runConf.BaseRunConfHandler;

public class Event {
    public static final String ID = null;
    public static final String VERSION = null;

    transient BaseRunConfHandler handler;

    @VisibleForTesting
    public void handle() {
    };

    public void setHandler(BaseRunConfHandler handler) {
        this.handler = handler;
    }
}
