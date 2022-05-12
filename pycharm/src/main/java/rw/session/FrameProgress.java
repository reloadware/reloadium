package rw.session;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import rw.dialogs.DialogFactory;
import rw.dialogs.FirstFrameError;
import rw.dialogs.TipDialog;
import rw.frame.FrameManager;
import rw.handler.runConf.BaseRunConfHandler;
import rw.handler.runConf.PythonRunConfHandler;

import java.io.File;
import java.util.Map;

public class FrameProgress extends FrameEvent {
    private static final Logger LOGGER = Logger.getInstance(FrameError.class);

    public static final String ID = "FrameProgress";
    public static final String VERSION = "0.1.0";

    private Integer line;
    @SerializedName("line_timing")
    private Map<Integer, Long> lineTiming;

    @Override
    public void handle() {
        LOGGER.debug("Handling FrameProgress " + String.format("(path=%s, frame=%d,  line=%d)",
                this.getPath(), this.getFrameId(), this.getLine()));
        super.handle();
        this.handler.getFrameManager().onFrameProgressEvent(this);
        this.handler.getProfilePreviewRenderer().update();
    }

    public Integer getLine() {
        return this.line;
    }

    public Map<Integer, Long> getLineTiming() {
        return this.lineTiming;
    }
}
