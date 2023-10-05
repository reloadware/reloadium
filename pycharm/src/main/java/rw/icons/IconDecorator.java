package rw.icons;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;

import javax.swing.*;

final class IconDecorator implements ProjectViewNodeDecorator {
    @Override
    public void decorate(ProjectViewNode<?> node, PresentationData data) {
        PreferencesState preferences = Preferences.get().getState();
        if (!preferences.markReloadable) {
            return;
        }

        Icon ret = IconPatcher.getIcon(node.getProject(), node.getVirtualFile(), data.getIcon(true));
        data.setIcon(ret);
    }
}
