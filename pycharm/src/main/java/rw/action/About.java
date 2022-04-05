package rw.action;

import com.intellij.ide.lightEdit.LightEditCompatible;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.action.AboutDialog;


public class About extends AnAction implements DumbAware, LightEditCompatible {
    public static String ID = "ReloadiumAbout";

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(true);
        super.update(e);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        perform(e.getData(CommonDataKeys.PROJECT));
    }

    public static void perform(@Nullable Project project) {
        new AboutDialog(project).show();
    }
}
