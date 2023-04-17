package rw.tests.fixtures;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.XSourcePositionImpl;
import com.jetbrains.python.debugger.*;
import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import rw.util.Architecture;


public class PyStackFrameFixture {
    private PyStackFrame frame;
    private final Project project;

    public PyStackFrameFixture(@NotNull Project project, @NotNull VirtualFile file, @NotNull String name) {
        this.project = project;

        PyDebugProcess debugProcess = Mockito.mock(PyDebugProcess.class);
        PyLocalPositionConverter positionConverter = new PyLocalPositionConverter();

        PySourcePosition sourcePosition = positionConverter.convertPythonToFrame(file.getPath(), 5);
        PyStackFrameInfo pyStackFrameInfo = new PyStackFrameInfo("thread", "frame_id", name, sourcePosition);

        this.frame = new PyStackFrame(this.project, debugProcess, pyStackFrameInfo,
                XDebuggerUtil.getInstance().createPosition(file, 5));
    }

    public PyStackFrame getFrame() {
        return this.frame;
    }

    public void start() {
    }

    public void stop() {
    }
}
