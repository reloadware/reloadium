package rw.session.events;

import com.intellij.openapi.diagnostic.Logger;

import java.util.List;
import java.util.Map;

public class StackUpdate extends Event {
    private static final Logger LOGGER = Logger.getInstance(StackUpdate.class);

    public static final String ID = "StackUpdate";

    private Map<String, List<FrameData>> content;

    @Override
    public void handle() {
        LOGGER.debug("Handling StackUpdate");

        for (List<FrameData> frames: this.content.values()) {
            for(FrameData f: frames) {
                f.localPath = this.handler.convertPathToLocal(f.getPath().toString(), false);
            }
        }

        super.handle();
        this.handler.getStack().onStackUpdateEvent(this);
    }

    public Map<String, List<FrameData>> getContent() {
        return content;
    }
}
