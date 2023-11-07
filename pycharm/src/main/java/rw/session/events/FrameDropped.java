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
    @SerializedName("line_end")
    final private Integer lineEnd;
    @SerializedName("line_start")
    final private Integer lineStart;

    public FrameDropped(@NotNull String path,
                        @NotNull VirtualFile file,
                        Integer lineEnd,
                        Integer lineStart) {
        super(path, file);
        this.lineEnd = lineEnd;
        this.lineStart = lineStart;
    }

    @Override
    public void handle() {
        super.handle();
        LOGGER.info("Handling FrameDopped");

        PreferencesState state = Preferences.get().getState();

        Color BLINK_COLOR = new Color(255, 0, 0, 60);

        Blink blink = new Blink(this.handler.getProject(), this.getFile(), this.lineStart,
                this.lineEnd,
                BLINK_COLOR, -2, state.blinkDuration);
        Blinker.get().blink(blink);
    }
}
