package rw.action;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import rw.config.Config;
import rw.service.Service;
import rw.pkg.WebPackageManager;
import rw.util.NotificationManager;


public class Update extends AnAction {
    public static String ID = "UpdatePackage";

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(true);

        super.update(e);
    }

    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = getEventProject(e);

        if (project == null)
            return;

        Service service = Service.get();

        if (service.getPackageManager().isInstalled()) {
            NotificationManager.get().show(project,
                    Config.get().msgs.ALREADY_UP_TO_DATE,
                    "",
                    NotificationType.INFORMATION);
            return;
        }

        service.getPackageManager().run(
                new WebPackageManager.Listener() {
                    @Override
                    public void started() {
                    }

                    @Override
                    public void success() {
                        NotificationManager.get().show(project,
                    Config.get().msgs.UPDATED_SUCCESSFULLY,
                    "",
                    NotificationType.INFORMATION);
                    }

                    @Override
                    public void fail(Exception exception) {
                        NotificationManager.get().show(project,
                    Config.get().msgs.INSTALLING_FAILED,
                    "",
                    NotificationType.ERROR);
                    }
                });
    }
}
