// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package rw.notification;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.impl.EditConfigurationsDialog;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotificationPanel.ActionHandler;
import com.intellij.ui.EditorNotifications;
import com.intellij.ui.LightColors;
import rw.config.Config;
import rw.handler.runConf.BaseRunConfHandler;
import rw.handler.runConf.RunConfHandlerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.HyperlinkEvent;

public final class NoCwdNotificationProvider extends EditorNotifications.Provider<EditorNotificationPanel> implements DumbAware {
    public static final Key<EditorNotificationPanel> KEY = Key.create("NoCwdNotification");

    @Override
    public @NotNull Key<EditorNotificationPanel> getKey() {
        return KEY;
    }

    @Override
    public @Nullable EditorNotificationPanel createNotificationPanel(@NotNull VirtualFile file,
                                                                     @NotNull FileEditor fileEditor,
                                                                     @NotNull Project project) {
        RunManagerImpl runManager = RunManagerImpl.getInstanceImpl(project);
        RunnerAndConfigurationSettings selectedConf = runManager.getSelectedConfiguration();

        if (selectedConf == null) {
            return null;
        }

        BaseRunConfHandler handler = RunConfHandlerFactory.factory(selectedConf.getConfiguration());

        if (handler == null) {
            return null;
        }

        if(!handler.getWorkingDirectory().isBlank()) {
            return null;
        }

        EditorNotificationPanel panel = new EditorNotificationPanel(LightColors.RED);
        panel.setText(Config.get().msgs.MISSING_CWD);
        panel.createActionLabel(Config.get().msgs.EDIT_CONFIGURATION, this.getFixHandler(project, file), true);
        return panel;
    }

    EditorNotificationPanel.ActionHandler getFixHandler(@NotNull Project project, @NotNull VirtualFile file) {
        return new ActionHandler() {
            @Override
            @Deprecated
            public void handlePanelActionClick(@NotNull EditorNotificationPanel panel,
                                               @NotNull HyperlinkEvent e) {
                EditConfigurationsDialog dialog = new EditConfigurationsDialog(project);
                dialog.show();
            }

            @Override
            @Deprecated
            public void handleQuickFixClick(@NotNull Editor editor, @NotNull PsiFile psiFile) {
                EditConfigurationsDialog dialog = new EditConfigurationsDialog(project);
                dialog.show();
            }
        };
    }
}
