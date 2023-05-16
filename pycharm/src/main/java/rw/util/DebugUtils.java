package rw.util;

import com.intellij.xdebugger.frame.XSuspendContext;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.jetbrains.python.debugger.PyExecutionStack;
import com.jetbrains.python.debugger.PyStackFrame;
import org.jetbrains.annotations.Nullable;

public class DebugUtils {
    public static @Nullable PyStackFrame getCurrentStackFrame(XDebugSessionImpl debugSession) {
        XSuspendContext suspendContext = debugSession.getSuspendContext();

        if (suspendContext == null) {
            return null;
        }

        PyExecutionStack executionStack = (PyExecutionStack) suspendContext.getActiveExecutionStack();
        if (executionStack == null) {
            return null;
        }

        PyStackFrame stackFrame = (PyStackFrame) debugSession.getCurrentStackFrame();
        return stackFrame;
    }

    public static @Nullable PyStackFrame getTopStackFrame(XDebugSessionImpl debugSession) {
        XSuspendContext suspendContext = debugSession.getSuspendContext();

        if (suspendContext == null) {
            return null;
        }

        PyExecutionStack executionStack = (PyExecutionStack) suspendContext.getActiveExecutionStack();

        if (executionStack == null) {
            return null;
        }

        PyStackFrame stackFrame = executionStack.getTopFrame();
        return stackFrame;
    }
}
