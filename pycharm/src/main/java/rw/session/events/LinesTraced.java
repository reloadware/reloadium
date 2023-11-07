package rw.session.events;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

public class LinesTraced extends FileEvent {
    public static final String ID = "LinesTraced";
    private static final Logger LOGGER = Logger.getInstance(ModuleUpdate.class);
    private String name;
    @SerializedName("line_end")
    final private Integer lineEnd;
    @SerializedName("line_start")
    final private Integer lineStart;
    @VisibleForTesting
    public LinesTraced(@NotNull String path, @NotNull VirtualFile file, @NotNull String name, Integer lineEnd,
                       Integer lineStart){
        super(path, file);
        this.name = name;
        this.lineEnd = lineEnd;
        this.lineStart = lineStart;
    }

    @Override
    public void handle() {
        super.handle();
        LOGGER.info("Handling LinesTraced");

        ApplicationManager.getApplication().invokeLater(() -> {
            this.handler.getFastDebug().onLinesTracedEvent(this);
        });
    }

    public String getName() {
        return this.name;
    }

    public int getLineStart() {
        return this.lineStart;
    }
    public int getLineEnd() {
        return this.lineEnd;
    }
}
