package rw.session.events;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.List;

public class LineProfileClear extends FileEvent {
    public static final String ID = "LineProfileClear";
    private static final Logger LOGGER = Logger.getInstance(LineProfileClear.class);
    final private List<List<Integer>> ranges;

    @VisibleForTesting
    public LineProfileClear(@NotNull String path,
                            @NotNull VirtualFile file,
                            List<List<Integer>> ranges) {
        super(path, file);
        this.ranges = ranges;
    }

    @Override
    public void handle() {
        LOGGER.debug("Handling LineProfileClear");
        super.handle();
        for (List<Integer> r : this.ranges) {
            this.handler.getTimeProfiler().clearLines(this.getFile(), r.get(0), r.get(1));
        }
    }
}
