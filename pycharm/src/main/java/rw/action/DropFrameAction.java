package rw.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.jetbrains.python.debugger.PyStackFrame;
import org.jetbrains.annotations.NotNull;
import rw.debugger.DropFrameActionHandler;
import rw.handler.RunConfHandler;
import rw.handler.RunConfHandlerManager;
import rw.session.cmds.DropFrame;


public class DropFrameAction extends AnAction implements DumbAware {
    private static final Logger LOGGER = Logger.getInstance(ContextPopupAction.class);

    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        assert e.getProject() != null;

        Project project = this.getEventProject(e);
        RunConfHandler handler = RunConfHandlerManager.get().getCurrentDebugHandler(project);

        boolean visible = handler != null;
        presentation.setVisible(visible);
        if (!visible) {
            return;
        }

        DropFrameActionHandler dropFrameActionHandler = new DropFrameActionHandler(handler.getSession(), handler.getStack());
        XDebugSessionImpl debugSession = ((XDebugSessionImpl) XDebuggerManager.getInstance(e.getProject()).getCurrentSession());

        if (debugSession == null) {
            return;
        }

        PyStackFrame frame = (PyStackFrame) debugSession.getCurrentStackFrame();

        if (frame == null) {
            return;
        }
        presentation.setEnabled(dropFrameActionHandler.canDrop(frame));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        assert e.getProject() != null;

        XDebugSessionImpl debugSession = ((XDebugSessionImpl) XDebuggerManager.getInstance(e.getProject()).getCurrentSession());
        assert debugSession != null;

        PyStackFrame frame = (PyStackFrame) debugSession.getCurrentStackFrame();
        assert frame != null;
        Project project = this.getEventProject(e);
        RunConfHandler handler = RunConfHandlerManager.get().getCurrentDebugHandler(project);

        assert handler != null;
        DropFrame cmd = new DropFrame(frame.getFrameId());
        handler.getSession().send(cmd, false);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
