package rw.action;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;


public class ReloadOnSave implements FileDocumentManagerListener {
    public ReloadOnSave() {
        super();
    }

    @Override
    public void beforeDocumentSaving(@NotNull Document document) {
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);

        Project project = null;

        if (virtualFile != null) {
            project = ProjectLocator.getInstance().guessProjectForFile(virtualFile);
        }

        ManualReload.handleSave(project, new Document[]{document});
    }

}
