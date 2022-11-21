package rw.session.events;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Map;

public class LineProfile extends FileEvent {
    private static final Logger LOGGER = Logger.getInstance(LineProfile.class);

    public static final String ID = "LineProfile";
    public static final String VERSION = "0.1.0";

    private Boolean stop;
    private Map<Integer, Long> values;
    private Map<Integer, String> display;
    private Integer line;

    @Override
    public void handle() {
        LOGGER.debug("Handling LineProfile");
        super.handle();
        this.handler.getStackRenderer().onFrameProgressEvent(this);
        this.handler.getTimeProfiler().onLineProfileEvent(this);
        this.handler.getProfilePreviewRenderer().update();
    }

    public Boolean isStop() {
        return this.stop;
    }

    public Map<Integer, Long> getValues() {
        return this.values;
    }

    public Map<Integer, String> getDisplay() {
        return this.display;
    }

    public Integer getLine() {
        return this.line;
    }
}
