package rw.session.events;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.VisibleForTesting;
import rw.icons.IconPatcher;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class WatchingConcluded extends Event {
    public static final String ID = "WatchingConcluded";

    @VisibleForTesting
    public WatchingConcluded() {
    }

    @Override
    public void handle() {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(this.handler.getProject());
        ApplicationManager.getApplication().invokeLater(() -> {
            statusBar.setInfo("Processing done");
        });

        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            statusBar.setInfo("");
        }).start();
    }
}
