package rw.session.events;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import rw.highlights.Blink;
import rw.highlights.Blinker;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;

import java.awt.*;

public class FrameDropped extends FileEvent {
    public static final String ID = "FrameDropped";
    private static final Logger LOGGER = Logger.getInstance(ModuleUpdate.class);
    @SerializedName("to_line")
    final private Integer toLine;
    @SerializedName("from_line")
    final private Integer fromLine;

    public FrameDropped(@NotNull String path,
                        @NotNull VirtualFile file,
                        Integer toLine,
                        Integer fromLine) {
        super(path, file);
        this.toLine = toLine;
        this.fromLine = fromLine;
    }

    @Override
    public void handle() {
        super.handle();
        LOGGER.info("Handling FrameDopped");

        PreferencesState state = Preferences.getInstance().getState();

        Color BLINK_COLOR = new Color(255, 0, 0, 60);

        Blink blink = new Blink(this.handler.getProject(), this.getFile(), this.fromLine,
                this.toLine,
                BLINK_COLOR, -2, state.blinkDuration);
        Blinker.get().blink(blink);
    }
}
