package rw.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;
import rw.settings.SettingsConfigurable;


public class Settings extends AnAction implements DumbAware {
    public static String ID = "ReloadiumSettings";

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(true);
        super.update(e);
    }

    public void actionPerformed(@NotNull AnActionEvent e) {
        ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), SettingsConfigurable.class);
    }
}
