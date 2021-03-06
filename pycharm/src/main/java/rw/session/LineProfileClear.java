package rw.session;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;

import java.util.List;
import java.util.Map;

public class LineProfileClear extends FileEvent {
    private static final Logger LOGGER = Logger.getInstance(LineProfileClear.class);

    public static final String ID = "LineProfileClear";
    public static final String VERSION = "0.1.0";

    private List<List<Integer>> ranges;

    @Override
    public void handle() {
        LOGGER.debug("Handling LineProfileClear");
        super.handle();
        for(List<Integer> r: this.ranges) {
            this.handler.getTimeProfiler().clearLines(this.getLocalPath(), r.get(0), r.get(1));
        }
    }
}
