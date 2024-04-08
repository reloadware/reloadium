package rw.session.events;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;
import rw.highlights.Blink;
import rw.highlights.Blinker;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;
import rw.profile.FrameProgressRenderer;

import java.awt.*;
import java.util.List;

public class ModuleUpdate extends FileEvent {
    public static final String ID = "ModuleUpdate";
    private static final Logger LOGGER = Logger.getInstance(ModuleUpdate.class);
    public List<Action> actions;

    @VisibleForTesting
    public ModuleUpdate(@NotNull String path, @NotNull VirtualFile file, List<Action> actions) {
        super(path, file);
        this.actions = actions;
    }

    @Override
    public void handle() {
        LOGGER.info("Handling ModuleUpdate " + String.format("(%s)", this.getFile()));
        PreferencesState state = Preferences.get().getState();

        Color BLINK_COLOR = new Color(255, 114, 0, 60);

        for (Action a : this.actions) {
            if (a.getLineStart() == -1) {
                continue;
            }

            if (a.getName().equals("Move") || a.getName().equals("Delete") || a.getObj().equals("Frame")) {
                continue;
            }

            if (a.shouldBlink()) {
                Blink blink = new Blink(this.handler.getProject(), this.getFile(), a.getLineStart(), a.getLineEnd(),
                        BLINK_COLOR, -3, state.blinkDuration);
                Blinker.get().blink(blink);
            }
        }
    }
}
