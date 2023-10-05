package rw.notification;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.icons.Icons;

public class RwNotification extends Notification {
    public RwNotification(@NotNull String title,
                          @NotNull String content,
                          boolean addDoNotShowAgainAction) {
        super("Reloadium", title, content, NotificationType.INFORMATION);
        this.setIcon(Icons.Notification);
        this.setImportantSuggestion(true);
        this.setImportant(true);

        this.addPrimaryAction();

        if(addDoNotShowAgainAction) {
            this.addAction(this.getDoNotShowAgainAction());
        }
    }

    private NotificationAction getDoNotShowAgainAction() {
        NotificationAction ret = new NotificationAction("Do not show again") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                NotificationState.get().setToBeShown(notification.getTitle(), false);
                notification.expire();
            }
        };
        return ret;
    }

    protected void addPrimaryAction() {
    }

    @Override
    public void notify(@Nullable Project project) {
        if(NotificationState.get().shouldShow(this.getTitle())) {
            super.notify(project);
        }
    }
}
