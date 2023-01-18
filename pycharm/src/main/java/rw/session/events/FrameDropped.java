package rw.session.events;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.diagnostic.Logger;
import rw.highlights.Blink;
import rw.highlights.Blinker;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;

import java.awt.*;

public class FrameDropped extends FrameEvent {
    public static final String ID = "FrameDropped";
    private static final Logger LOGGER = Logger.getInstance(ModuleUpdate.class);
    @Override
    public void handle() {
        super.handle();
        LOGGER.info("Handling FrameDopped " + String.format("(%s)", this.getFrameId()));

        this.handler.getErrorHighlightManager().clearFile(this.getLocalPath());

        PreferencesState state = Preferences.getInstance().getState();

        Color BLINK_COLOR = new Color(255, 0, 0, 60);

        Blink blink = new Blink(this.handler.getProject(), this.getLocalPath(), this.getHandlerLineno(),
                this.getLineno(),
                    BLINK_COLOR, -2, state.blinkDuration);
            Blinker.get().blink(blink);
    }
}
