package rw.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.intellij.xdebugger.impl.frame.XDebuggerFramesList;
import com.jetbrains.python.debugger.PyStackFrame;
import com.jetbrains.python.debugger.PyThreadInfo;
import org.jetbrains.annotations.NotNull;
import rw.debugger.DropFrameActionHandler;
import rw.debugger.StackFrame;
import rw.handler.runConf.BaseRunConfHandler;
import rw.handler.runConf.RunConfHandlerManager;
import rw.session.cmds.DropFrame;

import java.util.List;


public class DropFrameAction extends AnAction implements DumbAware {
    private static final Logger LOGGER = Logger.getInstance(ContextPopupAction.class);
    private static final DataKey<XDebuggerFramesList> FRAMES_LIST = DataKey.create("FRAMES_LIST");

    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();

        Project project = this.getEventProject(e);
        BaseRunConfHandler handler = RunConfHandlerManager.get().getCurrentHandler(project);

        boolean visible = handler != null;
        presentation.setVisible(visible);
        if(!visible) {
            return;
        }

        DropFrameActionHandler dropFrameActionHandler = new DropFrameActionHandler(handler.getSession(), handler.getStack());
        XDebugSessionImpl debugSession = ((XDebugSessionImpl) XDebuggerManager.getInstance(e.getProject()).getCurrentSession());

        PyStackFrame frame = (PyStackFrame) debugSession.getCurrentStackFrame();
        presentation.setEnabled(dropFrameActionHandler.canDrop(frame));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        XDebugSessionImpl debugSession = ((XDebugSessionImpl) XDebuggerManager.getInstance(e.getProject()).getCurrentSession());
        PyStackFrame frame = (PyStackFrame) debugSession.getCurrentStackFrame();
        Project project = this.getEventProject(e);
        BaseRunConfHandler handler = RunConfHandlerManager.get().getCurrentHandler(project);

        assert handler != null;
        DropFrame cmd = new DropFrame(frame.getFrameId());
        handler.getSession().send(cmd);
    }
}
