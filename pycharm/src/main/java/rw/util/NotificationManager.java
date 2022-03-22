package rw.util;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

public class NotificationManager {
    public void show(Project project,
                            @NotNull String title,
                            @NotNull String content,
                            @NotNull NotificationType type) {
        final RwNotification notification = new RwNotification(title, content,
                    type);
            ApplicationManager.getApplication().invokeLater(() -> {
                        notification.notify(project);
                    }
            );
    }

    @VisibleForTesting
    public static NotificationManager singleton = null;
    public NotificationManager() {

    }

    public static NotificationManager get() {
        if (singleton == null)
            singleton = new NotificationManager();

        return singleton;
    }
}
