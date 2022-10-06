package rw.icons;

import com.intellij.ide.FileIconPatcher;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import com.intellij.ide.projectView.impl.AbstractProjectViewPane;
import com.intellij.ide.projectView.impl.ProjectViewImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.EditorTabPreview;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.packageDependencies.ui.PackageDependenciesNode;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.IconManager;
import com.intellij.ui.LayeredIcon;
import rw.handler.runConf.BaseRunConfHandler;
import rw.handler.runConf.RunConfHandlerManager;

import javax.swing.*;
import java.io.File;

public class IconPatcher implements FileIconPatcher, DumbAware {
    static public Icon getIcon(Project project, VirtualFile file, Icon baseIcon) {
        BaseRunConfHandler handler = RunConfHandlerManager.get().getCurrentHandler(project);
        if (handler == null) {
            handler = RunConfHandlerManager.get().getLastHandler();
        }
        if (handler == null) {
            return baseIcon;
        }

        if (!handler.isWatched(new File(file.getPath()))) {
            return baseIcon;
        }

        final Icon icon = IconManager.getInstance().createLayered(new LayeredIcon(baseIcon, Icons.Reloadable));
        return icon;
    }

    static public void refresh(Project project) {
        ProjectView projectView = ProjectView.getInstance(project);
        AbstractProjectViewPane viewPane = projectView.getCurrentProjectViewPane();
        viewPane.updateFromRoot(true);

        FileEditorManagerEx.getInstanceEx(project).refreshIcons();

    }

    public Icon patchIcon(final Icon baseIcon, final VirtualFile file, final int flags, final Project project) {
        Icon ret = IconPatcher.getIcon(project, file, baseIcon);
        return ret;
    }
}
