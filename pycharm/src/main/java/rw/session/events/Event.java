package rw.session.events;

import org.jetbrains.annotations.VisibleForTesting;
import rw.handler.RunConfHandler;

public class Event {
    public static final String ID = null;

    transient RunConfHandler handler;

    @VisibleForTesting
    public void handle() {
    }

    public boolean isValid() {
        return true;
    }

    public void setHandler(RunConfHandler handler) {
        this.handler = handler;
    }
}
