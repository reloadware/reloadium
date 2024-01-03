package rw.debugger;

import com.intellij.xdebugger.frame.XDropFrameHandler;
import com.intellij.xdebugger.frame.XStackFrame;
import com.jetbrains.python.debugger.PyStackFrame;
import org.jetbrains.annotations.NotNull;
import rw.session.Session;
import rw.session.cmds.DropFrame;
import rw.stack.Frame;
import rw.stack.Stack;

public class DropFrameActionHandler implements XDropFrameHandler {
    private Session session;
    private Stack stack;

    public DropFrameActionHandler(Session session, Stack stack) {
        this.session = session;
        this.stack = stack;
    }

    @Override
    public boolean canDrop(@NotNull XStackFrame frame) {
        PyStackFrame pyStackFrame = (PyStackFrame) frame;

        Frame reFrame = this.stack.getFrameById(Long.valueOf(pyStackFrame.getFrameId()));

        if(reFrame == null){
            return false;
        }

        boolean ret = reFrame.isDroppable();
        return ret;
    }

    @Override
    public void drop(@NotNull XStackFrame frame) {
        this.session.send(new DropFrame(((StackFrame) frame).getFrameId()), false);
    }
}
