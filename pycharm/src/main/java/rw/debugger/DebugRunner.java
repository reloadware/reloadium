package rw.debugger;

import com.intellij.execution.ExecutionResult;
import com.intellij.xdebugger.XDebugSession;
import com.jetbrains.python.debugger.PyDebugProcess;
import com.jetbrains.python.debugger.PyDebugRunner;
import com.jetbrains.python.run.PythonCommandLineState;
import org.jetbrains.annotations.NotNull;
import rw.handler.RunConfHandler;

import java.net.ServerSocket;

public class DebugRunner extends PyDebugRunner {
    private RunConfHandler handler;

    public DebugRunner(RunConfHandler handler) {
        this.handler = handler;
    }

    @NotNull
    protected PyDebugProcess createDebugProcess(@NotNull XDebugSession debugSession,
                                                ServerSocket serverSocket,
                                                ExecutionResult result,
                                                PythonCommandLineState pyState) {
        return new DebugProcess(debugSession, this.handler, serverSocket, result.getExecutionConsole(), result.getProcessHandler(),
                pyState.isMultiprocessDebug());
    }
}
