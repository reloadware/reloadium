package rw.session.events;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.diagnostic.Logger;

abstract public class ThreadEvent extends Event {
    private static final Logger LOGGER = Logger.getInstance(ThreadEvent.class);

    @SerializedName("thread_id")
    private String threadId;

    public String getThreadId() {
        return this.threadId;
    }
}
