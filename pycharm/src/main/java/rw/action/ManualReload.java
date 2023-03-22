package rw.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.python.psi.impl.PyFileImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.handler.BaseRunConfHandler;
import rw.handler.RunConfHandlerManager;
import rw.session.cmds.ReloadFile;

import java.util.List;


public class ManualReload extends AnAction implements DumbAware {
    private static final Logger LOGGER = Logger.getInstance(ContextPopupAction.class);

    public void update(@NotNull AnActionEvent e) {
        List<BaseRunConfHandler> handlers = RunConfHandlerManager.get().getAllActiveHandlers(e.getProject());
        @Nullable Object data = e.getDataContext().getData("psi.File");

        boolean isEnabled = !handlers.isEmpty() && data != null;

        Presentation presentation = e.getPresentation();
        presentation.setVisible(true);
        presentation.setEnabled(isEnabled);
    }
    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.BGT;
  }

    public static void handleSave(@Nullable Project project, @NotNull Document[] documents) {
        ApplicationManager.getApplication().invokeLater(() -> {
            List<BaseRunConfHandler> handlers = RunConfHandlerManager.get().getAllActiveHandlers(project);
            if (handlers.isEmpty()) {
                return;
            }
            FileDocumentManager manager = FileDocumentManager.getInstance();

            for (Document d : documents) {
                manager.saveDocument(d);

                VirtualFile file = FileDocumentManager.getInstance().getFile(d);

                handlers.forEach(h -> {
                    ReloadFile cmd = new ReloadFile(h.convertPathToRemote(file.getPath(), true), d.getText());
                    h.getSession().send(cmd);
                });
            }
        });
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PyFileImpl data;
        try {
            data = (PyFileImpl) e.getDataContext().getData("psi.File");
        }
        catch (ClassCastException ignored) {
            return;
        }

        if (data == null) {
            return;
        }

        VirtualFile file = data.getVirtualFile();

        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();

        Document document = fileDocumentManager.getDocument(file);
        fileDocumentManager.saveDocument(document);

        ManualReload.handleSave(e.getProject(), new Document[]{document});
    }
}
