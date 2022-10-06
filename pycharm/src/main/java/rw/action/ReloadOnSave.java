package rw.action;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;


public class ReloadOnSave implements FileDocumentManagerListener {
    public ReloadOnSave() {
        super();
    }

    @Override
    public void beforeDocumentSaving(@NotNull Document document) {
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
        Project project = ProjectUtil.guessProjectForFile(virtualFile);

        if (project == null) {
            return;
        }

        ManualReload.handleSave(project, new Document[]{document});
    }
}
