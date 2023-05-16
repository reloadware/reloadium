package rw.debugger;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.frame.XDropFrameHandler;
import com.jetbrains.python.debugger.PyDebugProcess;
import com.jetbrains.python.debugger.PyStackFrame;
import com.jetbrains.python.debugger.PyStackFrameInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.handler.RunConfHandler;

import java.net.ServerSocket;

public class DebugProcess extends PyDebugProcess {
    private final DropFrameActionHandler dropFrameActionHandler;
    private final RunConfHandler handler;

    public DebugProcess(@NotNull XDebugSession debugSession, RunConfHandler handler, @NotNull ServerSocket serverSocket, @NotNull ExecutionConsole executionConsole, @Nullable ProcessHandler processHandler, boolean multiProcess) {
        super(debugSession, serverSocket, executionConsole, processHandler, multiProcess);
        this.handler = handler;
        this.dropFrameActionHandler = new DropFrameActionHandler(handler.getSession(), handler.getStack());
    }

    public PyStackFrame createStackFrame(PyStackFrameInfo frameInfo) {
        final PyStackFrame frame = new StackFrame(this.handler.getStack(), getSession().getProject(), this, frameInfo,
                getPositionConverter().convertFromPython(frameInfo.getPosition(), frameInfo.getName()));
        return frame;
    }

    public XDropFrameHandler getDropFrameHandler() {
        return this.dropFrameActionHandler;
    }
}
