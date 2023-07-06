package rw.session.events;

import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.VisibleForTesting;
import rw.icons.IconPatcher;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class WatchingPaths extends Event {
    public static final String ID = "WatchingPaths";

    final private List<String> paths;

    @VisibleForTesting
    public WatchingPaths(List<String> paths) {
        this.paths = paths;
    }

    @Override
    public void handle() {
        this.handler.addWatched(this.paths.stream().map(e -> new File(this.handler.convertPathToLocal(e, false))).collect(Collectors.toSet()));
        IconPatcher.refresh(this.handler.getProject());

        new Thread(this::sendEvents).start();
    }

    public void sendEvents() {
        List<String> files = this.paths.stream().filter(p -> p.endsWith(".py")).toList();
        float interval = 0.5f;
        float fileDelay = interval / files.size();
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(this.handler.getProject());

        if (fileDelay <= 0.05f) {
            fileDelay = 0.05f;
        }

        for (String f : files) {

            if (statusBar == null) {
                return;
            }
            statusBar.setInfo("Processing: %s".formatted(f));
            interval -= fileDelay;
            try {
                Thread.sleep((long) (fileDelay * 1e3));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (interval < 0.0f) {
                break;
            }
        }
    }
}
