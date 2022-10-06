package rw.session.events;

import com.intellij.openapi.diagnostic.Logger;

import java.util.Map;

public class LineProfile extends FileEvent {
    private static final Logger LOGGER = Logger.getInstance(LineProfile.class);

    public static final String ID = "LineProfile";
    public static final String VERSION = "0.1.0";

    private Boolean stop;
    private Map<Integer, Long> timing;
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

    public Map<Integer, Long> getTiming() {
        return this.timing;
    }

    public Integer getLine() {
        return this.line;
    }
}
