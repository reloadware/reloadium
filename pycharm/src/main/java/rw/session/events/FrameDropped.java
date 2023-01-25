package rw.session.events;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.diagnostic.Logger;
import rw.highlights.Blink;
import rw.highlights.Blinker;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;

import java.awt.*;

public class FrameDropped extends FileEvent {
    public static final String ID = "FrameDropped";

    @SerializedName("to_line")
    private Integer toLine;

    @SerializedName("from_line")
    private Integer fromLine;

    private static final Logger LOGGER = Logger.getInstance(ModuleUpdate.class);
    @Override
    public void handle() {
        super.handle();
        LOGGER.info("Handling FrameDopped");

        PreferencesState state = Preferences.getInstance().getState();

        Color BLINK_COLOR = new Color(255, 0, 0, 60);

        Blink blink = new Blink(this.handler.getProject(), this.getLocalPath(), this.fromLine,
                this.toLine,
                    BLINK_COLOR, -2, state.blinkDuration);
            Blinker.get().blink(blink);
    }
}
