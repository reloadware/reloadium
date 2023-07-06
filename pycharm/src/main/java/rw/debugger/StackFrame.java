package rw.debugger;

import com.intellij.openapi.project.Project;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.xdebugger.XSourcePosition;
import com.jetbrains.python.debugger.PyFrameAccessor;
import com.jetbrains.python.debugger.PyStackFrame;
import com.jetbrains.python.debugger.PyStackFrameInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.icons.Icons;
import rw.stack.Frame;
import rw.stack.Stack;

public class StackFrame extends PyStackFrame {
    Stack stack;

    public StackFrame(Stack stack, @NotNull Project project, @NotNull PyFrameAccessor debugProcess, @NotNull PyStackFrameInfo frameInfo, XSourcePosition position) {
        super(project, debugProcess, frameInfo, position);
        this.stack = stack;
    }

    @Override
    public void customizePresentation(@NotNull ColoredTextContainer component) {
        Frame reFrame = this.stack.getFrameById(Long.valueOf(this.getFrameId()));

        super.customizePresentation(component);
        if (reFrame != null && reFrame.isReloadable()) {
            component.setIcon(Icons.Frame);
        }
    }
}
