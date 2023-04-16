package rw.tests.fixtures;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import org.jetbrains.annotations.NotNull;
import rw.handler.PythonRunConfHandler;
import rw.handler.RunConfHandler;
import rw.handler.RunConfHandlerFactory;
import rw.handler.RunConfHandlerManager;

import static org.mockito.Mockito.*;


public class DebugPythonRunConfHandlerFixture {
    private final Project project;
    private PythonRunConfHandler handler;

    public DebugPythonRunConfHandlerFixture(@NotNull Project project, @NotNull RunConfiguration runConfiguration) {
        this.project = project;

        PythonRunConfHandler handler = (PythonRunConfHandler) RunConfHandlerFactory.factory(runConfiguration);
        XDebugSessionImpl debugSession = mock(XDebugSessionImpl.class);

        this.handler = spy(handler);
        lenient().doReturn(debugSession).when(this.handler).getDebugSession();
    }

    public PythonRunConfHandler getHandler() {
        return this.handler;
    }

    public void start() {
    }

    public void stop() {
    }
}
