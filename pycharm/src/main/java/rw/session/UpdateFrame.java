package rw.session;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import rw.frame.FrameManager;
import rw.handler.runConf.BaseRunConfHandler;
import rw.handler.runConf.PythonRunConfHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateFrame extends FrameEvent {
    private static final Logger LOGGER = Logger.getInstance(UpdateModule.class);

    public static final String ID = "UpdateFrame";
    public static final String VERSION = "0.1.0";

    @Override
    public void handle() {
        LOGGER.info("Handling UpdateFrame " + String.format("(%s)", this.getPath()));
    }
}
