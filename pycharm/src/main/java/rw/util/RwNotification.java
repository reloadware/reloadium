package rw.util;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import org.jetbrains.annotations.NotNull;
import rw.icons.Icons;

public class RwNotification extends Notification {
    public RwNotification(@NotNull String title,
                          @NotNull String content,
                          @NotNull NotificationType type) {
        super("Reloadium", title, content, type);
        this.setIcon(Icons.ProductIcon);
    }
}
