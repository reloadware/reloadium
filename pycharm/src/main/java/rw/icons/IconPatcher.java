package rw.icons;

import com.intellij.ide.FileIconPatcher;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.AbstractProjectViewPane;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.IconManager;
import com.intellij.ui.LayeredIcon;
import rw.handler.BaseRunConfHandler;
import rw.handler.RunConfHandlerManager;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class IconPatcher implements FileIconPatcher, DumbAware {
    static public Icon getIcon(Project project, VirtualFile file, Icon baseIcon) {
        PreferencesState preferences = Preferences.getInstance().getState();
        if(!preferences.markReloadable) {
            return baseIcon;
        }

        List<BaseRunConfHandler> handlers = RunConfHandlerManager.get().getAllActiveHandlers(project);
        for (BaseRunConfHandler h : handlers) {
            if (!h.isWatched(new File(file.getPath()))) {
                continue;
            }

            Icon reloadableIcon;

            if (file.isDirectory()) {
                 reloadableIcon = Icons.ReloadableDir;
            }
            else {
                reloadableIcon = Icons.ReloadableFile;
            }

            final Icon icon = IconManager.getInstance().createLayered(new LayeredIcon(baseIcon, reloadableIcon));
            return icon;
        }
        return baseIcon;
    }

    static public void refresh(Project project) {
        ProjectView projectView = ProjectView.getInstance(project);
        AbstractProjectViewPane viewPane = projectView.getCurrentProjectViewPane();
        if (viewPane != null) {
            viewPane.updateFromRoot(true);
        }

        FileEditorManagerEx.getInstanceEx(project).refreshIcons();

    }

    public Icon patchIcon(final Icon baseIcon, final VirtualFile file, final int flags, final Project project) {
        Icon ret = IconPatcher.getIcon(project, file, baseIcon);
        return ret;
    }
}
