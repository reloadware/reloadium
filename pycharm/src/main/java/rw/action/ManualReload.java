package rw.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.intellij.xdebugger.impl.breakpoints.XBreakpointManagerImpl;
import com.intellij.xdebugger.impl.breakpoints.XLineBreakpointImpl;
import com.intellij.xdebugger.impl.breakpoints.XLineBreakpointManager;
import com.jetbrains.python.debugger.PyDebugProcess;
import com.jetbrains.python.psi.impl.PyFileImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.handler.RunConfHandler;
import rw.handler.RunConfHandlerManager;
import rw.session.cmds.ReloadFile;

import java.util.List;


public class ManualReload extends AnAction implements DumbAware {
    private static final Logger LOGGER = Logger.getInstance(ContextPopupAction.class);

    public static void handleSave(@Nullable Project project, @NotNull Document[] documents) {
        ApplicationManager.getApplication().invokeLater(() -> {
            List<RunConfHandler> handlers = RunConfHandlerManager.get().getAllActiveHandlers(project);
            if (handlers.isEmpty()) {
                return;
            }
            FileDocumentManager manager = FileDocumentManager.getInstance();

            for (Document d : documents) {
                manager.saveDocument(d);

                VirtualFile file = FileDocumentManager.getInstance().getFile(d);

                if (file == null) {
                    continue;
                }

                handlers.forEach(h -> {
                    XBreakpointManagerImpl breakpointManager = (XBreakpointManagerImpl) XDebuggerManager.getInstance(h.getProject()).getBreakpointManager();
                    XLineBreakpointManager lineBreakpointManager = breakpointManager.getLineBreakpointManager();

                    lineBreakpointManager.getDocumentBreakpoints(d).forEach(XLineBreakpointImpl::updatePosition);

                    ReloadFile cmd = new ReloadFile(h.convertPathToRemote(file.getPath(), true), d.getText());
                    h.getSession().send(cmd, false);
                });
            }
        });
    }

    public void update(@NotNull AnActionEvent e) {
        List<RunConfHandler> handlers = RunConfHandlerManager.get().getAllActiveHandlers(e.getProject());
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

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PyFileImpl data;
        try {
            data = (PyFileImpl) e.getDataContext().getData("psi.File");
        } catch (ClassCastException ignored) {
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
