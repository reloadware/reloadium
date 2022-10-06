package rw.action;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.util.TimeoutUtil;
import com.jetbrains.python.psi.impl.PyFileImpl;
import org.jetbrains.annotations.NotNull;
import rw.handler.runConf.BaseRunConfHandler;
import rw.handler.runConf.RunConfHandlerManager;
import rw.session.cmds.ReloadFile;

import java.util.List;


public class ManualReload extends AnAction implements UpdateInBackground {
    private static final Logger LOGGER = Logger.getInstance(ContextPopupAction.class);

    public void update(@NotNull AnActionEvent e) {
        List<BaseRunConfHandler> handlers = RunConfHandlerManager.get().getAllActiveHandlers(e.getProject());
        boolean isEnabled = !handlers.isEmpty();

        Presentation presentation = e.getPresentation();
        presentation.setVisible(true);
        presentation.setEnabled(isEnabled);
    }

    public static void handleSave(@NotNull Project project, @NotNull Document[] documents) {
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
                    ReloadFile cmd = new ReloadFile(h.convertPathToRemote(file.getPath()));
                    h.getSession().send(cmd);
                });
            }
        });
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        VirtualFile file = ((PyFileImpl)e.getDataContext().getData("psi.File")).getVirtualFile();

        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();

        Document document = fileDocumentManager.getDocument(file);
        fileDocumentManager.saveDocument(document);

        ManualReload.handleSave(e.getProject(), new Document[]{document});
    }
}
