package rw.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.intellij.xdebugger.impl.frame.XDebuggerFramesList;
import com.jetbrains.python.debugger.PyStackFrame;
import org.jetbrains.annotations.NotNull;


public class RestartFrame extends AnAction implements DumbAware {
    private static final Logger LOGGER = Logger.getInstance(ContextPopupAction.class);
    private static final DataKey<XDebuggerFramesList> FRAMES_LIST = DataKey.create("FRAMES_LIST");

    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setVisible(true);
        presentation.setEnabled(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        XDebugSessionImpl debugSession = ((XDebugSessionImpl) XDebuggerManager.getInstance(e.getProject()).getCurrentSession());
        PyStackFrame frame = (PyStackFrame) debugSession.getCurrentStackFrame();
        SimpleColoredComponent builder = new SimpleColoredComponent();

        XDebuggerFramesList framesList = e.getData(FRAMES_LIST);
    }
}
