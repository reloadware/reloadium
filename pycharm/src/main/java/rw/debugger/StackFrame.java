package rw.debugger;

import com.intellij.openapi.project.Project;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.xdebugger.XSourcePosition;
import com.jetbrains.python.debugger.PyFrameAccessor;
import com.jetbrains.python.debugger.PyStackFrame;
import com.jetbrains.python.debugger.PyStackFrameInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.handler.runConf.BaseRunConfHandler;
import rw.icons.Icons;
import rw.stack.Frame;
import rw.stack.Stack;

public class StackFrame extends PyStackFrame {
    @Nullable private final Frame reFrame;
    public StackFrame(Stack stack, @NotNull Project project, @NotNull PyFrameAccessor debugProcess, @NotNull PyStackFrameInfo frameInfo, XSourcePosition position) {
        super(project, debugProcess, frameInfo, position);
        this.reFrame = stack.getById(Long.valueOf(this.getFrameId()));
    }

    @Override
    public void customizePresentation(@NotNull ColoredTextContainer component) {
        super.customizePresentation(component);
        if( this.reFrame != null ){
            component.setIcon(Icons.Frame);
        }
    }
}
