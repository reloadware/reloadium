package rw.session.events;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import rw.dialogs.DialogsState;
import rw.notification.GetProNotification;
import rw.notification.RwNotification;
import rw.notification.RwNotificationType;

import java.util.Map;

import static java.util.Map.entry;

public class ShowNotification extends Event {
    public static final String ID = "ShowNotification";
    private static final Logger LOGGER = Logger.getInstance(ShowNotification.class);

    String title;
    String message;
    String type;

    static private final Map<RwNotificationType, Class<? extends RwNotification>> typeToClass = Map.ofEntries(
            entry(RwNotificationType.GET_PRO, GetProNotification.class),
            entry(RwNotificationType.GENERIC, RwNotification.class)
        );

    @VisibleForTesting
    public ShowNotification(@NotNull String title, @NotNull String message, @Nullable String type) {
        this.title = title;
        this.message = message;
        this.type = type;
    }

    @Override
    public void handle() {
        super.handle();

        Class<? extends RwNotification> notificationClass = this.typeToClass.getOrDefault(
                RwNotificationType.fromString(this.type),
                RwNotification.class);

        try {
            RwNotification notification = notificationClass.getConstructor(String.class, String.class, boolean.class).newInstance(this.title, this.message, true);
            notification.notify(null);
        } catch (Exception e) {
            LOGGER.error("Failed to show notification", e);
        }
    }
}
