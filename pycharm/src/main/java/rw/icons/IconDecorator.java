package rw.icons;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import com.intellij.ide.projectView.impl.AbstractProjectViewPane;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.packageDependencies.ui.PackageDependenciesNode;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.IconManager;
import com.intellij.ui.LayeredIcon;
import rw.handler.runConf.BaseRunConfHandler;
import rw.handler.runConf.RunConfHandlerManager;

import javax.swing.*;
import java.io.File;

final class IconDecorator implements ProjectViewNodeDecorator {
    @Override
    public void decorate(ProjectViewNode<?> node, PresentationData data) {
        Icon ret = IconPatcher.getIcon(node.getProject(), node.getVirtualFile(), data.getIcon(true));
        data.setIcon(ret);
    }

    @Override
    public void decorate(PackageDependenciesNode node, ColoredTreeCellRenderer cellRenderer) {
    }
}
