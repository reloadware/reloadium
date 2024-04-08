package rw.action;

import com.intellij.ide.lightEdit.LightEditCompatible;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.dialogs.AboutDialog;


public class About extends AnAction implements DumbAware, LightEditCompatible {
    public static String ID = "ReloadiumAbout";

    public static void perform(@Nullable Project project) {
        new AboutDialog(project).show();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(true);
        super.update(e);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        perform(e.getData(CommonDataKeys.PROJECT));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
