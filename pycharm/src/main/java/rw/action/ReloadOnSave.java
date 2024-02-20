package rw.action;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;


public class ReloadOnSave implements FileDocumentManagerListener {
    public ReloadOnSave() {
        super();
    }

    @Override
    public void beforeDocumentSaving(@NotNull Document document) {
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);

        if (virtualFile != null) {
            Collection<Project> projects = ProjectLocator.getInstance().getProjectsForFile(virtualFile);

            for(Project project : projects) {
                ManualReload.handleSave(project, new Document[]{document});
            }
        }
        else {
            ManualReload.handleSave(null, new Document[]{document});
        }
    }
}
