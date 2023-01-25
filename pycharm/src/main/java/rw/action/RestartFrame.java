package rw.action;

import com.intellij.debugger.ui.DebuggerContentInfo;
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
import com.intellij.util.ui.TextTransferable;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.intellij.xdebugger.impl.frame.XDebuggerFramesList;
import com.intellij.xdebugger.impl.frame.XFramesView;
import com.intellij.xdebugger.impl.ui.XDebugSessionTab;
import com.jetbrains.python.debugger.PyDebugProcess;
import com.jetbrains.python.debugger.PyExecutionStack;
import com.jetbrains.python.debugger.PyStackFrame;
import com.jetbrains.python.psi.impl.PyFileImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.handler.runConf.BaseRunConfHandler;
import rw.handler.runConf.RunConfHandlerManager;
import rw.icons.Icons;
import rw.session.cmds.ReloadFile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


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
