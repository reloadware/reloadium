package rw.stack;

import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.frame.XSuspendContext;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.jetbrains.python.debugger.PyExecutionStack;
import org.jetbrains.annotations.Nullable;
import rw.handler.Activable;
import rw.session.events.ClearThreadError;
import rw.session.events.ThreadErrorEvent;

import java.util.HashMap;
import java.util.Map;

public class ThreadErrorManager implements Activable {
    Project project;
    Map<String, ThreadError> threadErrors;
    XDebugSessionImpl session;

    public ThreadErrorManager(Project project) {
        this.project = project;
        this.threadErrors = new HashMap<>();
    }
    public void onSessionStarted(XDebugSessionImpl session) {
        this.session = session;
        assert this.session != null;

        ThreadErrorManager This = this;

        session.addSessionListener(new XDebugSessionListener() {
            @Override
            public void stackFrameChanged() {
                This.onThreadChanged(This.getActiveThread());
            }
        });
    }

    public void onThreadError(ThreadErrorEvent threadErrorEvent) {
        ThreadError threadError = new ThreadError(this.project,
                threadErrorEvent.getThreadId(),
                threadErrorEvent.getLocalPath(),
                threadErrorEvent.getMsg(),
                threadErrorEvent.getLine());
        this.threadErrors.values().forEach(ThreadError::deactivate);
        threadError.activate();
        this.threadErrors.put(threadErrorEvent.getThreadId(), threadError);
    }

    public void onThreadErrorClear(ClearThreadError clearThreadError) {
        ThreadError threadError = this.threadErrors.get(clearThreadError.getThreadId());

        if(threadError == null) {
            return;
        }

        threadError.deactivate();
        this.threadErrors.remove(clearThreadError.getThreadId());
    }

    void onThreadChanged(String threadId) {
        ThreadError threadError = this.threadErrors.get(threadId);

        if (threadError == null) {
            return;
        }

        this.threadErrors.values().stream().filter(te -> !te.getThreadId().equals(threadId)).forEach(ThreadError::deactivate);

        if(!threadError.isActive()) {
            threadError.activate();
        }
    }

    public @Nullable String getActiveThread() {
        XSuspendContext suspendContext = this.session.getSuspendContext();
        if (suspendContext == null) {
            return null;
        }

        PyExecutionStack executionStack = (PyExecutionStack) this.session.getSuspendContext().getActiveExecutionStack();
        assert executionStack != null;

        String threadId = executionStack.getThreadId();
        return threadId;
    }

    @Override
    public void activate() {
        String activeThread = this.getActiveThread();
        if(activeThread == null) {
            return;
        }

        ThreadError threadError = this.threadErrors.get(activeThread);

        if(threadError == null) {
            return;
        }

        threadError.activate();
    }

    @Override
    public void deactivate() {
        this.threadErrors.values().forEach(ThreadError::deactivate);
    }
}
