package rw.notification;

import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.consts.Const;
import rw.icons.Icons;
import rw.lang.RwBundle;

public class GetProNotification extends RwNotification {
    public GetProNotification(@NotNull String title,
                              @NotNull String content,
                              boolean addDoNotShowAgainAction) {
        super(title, content, addDoNotShowAgainAction);
    }

    @Override
    protected void addPrimaryAction() {
        NotificationAction action = new NotificationAction(RwBundle.message("get.pro")) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                String url = Const.get().pricingUrl.toString();
                BrowserUtil.browse(url);
            }
        };
        this.addAction(action);
    }
}
