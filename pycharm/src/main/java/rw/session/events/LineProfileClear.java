package rw.session.events;

import com.intellij.openapi.diagnostic.Logger;

import java.util.List;

public class LineProfileClear extends FileEvent {
    public static final String ID = "LineProfileClear";
    private static final Logger LOGGER = Logger.getInstance(LineProfileClear.class);
    private List<List<Integer>> ranges;

    @Override
    public void handle() {
        LOGGER.debug("Handling LineProfileClear");
        super.handle();
        for (List<Integer> r : this.ranges) {
            this.handler.getTimeProfiler().clearLines(this.getLocalPath(), r.get(0), r.get(1));
        }
    }
}
