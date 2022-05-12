package rw.session;

import com.intellij.openapi.diagnostic.Logger;
import rw.highlights.Blink;
import rw.highlights.Blinker;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;

import java.awt.*;
import java.util.List;

public class UpdateModule extends FileEvent {
    private static final Logger LOGGER = Logger.getInstance(UpdateModule.class);

    public static final String ID = "UpdateModule";
    public static final String VERSION = "0.1.0";

    Color BLINK_COLOR = new Color(255, 114, 0, 40);

    public List<Action> actions;

    @Override
    public void handle() {
        LOGGER.info("Handling UpdateModule " + String.format("(%s)", this.getPath()));
        PreferencesState state = Preferences.getInstance().getState();

        this.handler.getErrorHighlightManager().clearAll();

        for (Action a : this.actions) {
            if (a.getLineStart() == -1) {
                continue;
            }

            if (a.getName().equals("Move") || a.getName().equals("Delete")) {
                continue;
            }

            Blink blink = new Blink(this.handler.getProject(), this.getLocalPath(), a.getLineStart(), a.getLineEnd(),
                    this.BLINK_COLOR, 0, state.blinkDuration);
            Blinker.get().blink(blink);
        }

    }
}
