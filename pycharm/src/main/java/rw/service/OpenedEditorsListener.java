package rw.service;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import rw.action.ManualReload;
import rw.handler.RunConfHandler;
import rw.session.cmds.UpdateOpenFilesCmd;
import rw.handler.RunConfHandlerManager;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class OpenedEditorsListener implements FileEditorManagerListener {
    public static List<String> getActiveFiles(@NotNull RunConfHandler handler, @NotNull Project project) {
        FileEditor [] editors = FileEditorManager.getInstance(project).getSelectedEditors();

        return Arrays.stream(editors).map(e -> handler.convertPathToRemote(e.getFile().getPath(), false)).toList();
    }

    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        this.notifyOpenedChanged(source.getProject());
    }

    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        this.notifyOpenedChanged(source.getProject());
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        this.notifyOpenedChanged(event.getManager().getProject());
    }

    private void notifyOpenedChanged(@NotNull Project project) {
        for (RunConfHandler handler : RunConfHandlerManager.get().getAllActiveHandlers(project)) {
            List<String> files = getActiveFiles(handler, project);
            handler.getSession().send(new UpdateOpenFilesCmd(files), false);
        }
    }
}
