package rw.session.events;

import com.intellij.openapi.diagnostic.Logger;
import rw.highlights.Blink;
import rw.highlights.Blinker;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;

import java.awt.*;
import java.util.List;

public class ModuleUpdate extends FileEvent {
    public static final String ID = "ModuleUpdate";
    private static final Logger LOGGER = Logger.getInstance(ModuleUpdate.class);
    public List<Action> actions;

    @Override
    public void handle() {
        LOGGER.info("Handling ModuleUpdate " + String.format("(%s)", this.getLocalPath()));
        PreferencesState state = Preferences.getInstance().getState();

        Color BLINK_COLOR = new Color(255, 114, 0, 60);

        for (Action a : this.actions) {
            if (a.getLineStart() == -1) {
                continue;
            }

            if (a.getName().equals("Move") || a.getName().equals("Delete") || a.getObj().equals("Frame")) {
                continue;
            }

            if (a.shouldBlink()) {
                Blink blink = new Blink(this.handler.getProject(), this.getLocalPath(), a.getLineStart(), a.getLineEnd(),
                        BLINK_COLOR, -2, state.blinkDuration);
                Blinker.get().blink(blink);
            }
        }
    }
}
