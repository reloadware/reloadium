package rw.session.events;

import org.jetbrains.annotations.VisibleForTesting;
import rw.handler.BaseRunConfHandler;

public class Event {
    public static final String ID = null;

    transient BaseRunConfHandler handler;

    @VisibleForTesting
    public void handle() {
    };

    public void setHandler(BaseRunConfHandler handler) {
        this.handler = handler;
    }
}
