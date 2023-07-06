package rw.stack;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
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
    private static final Logger LOGGER = Logger.getInstance(ThreadErrorManager.class);
    Project project;
    Map<String, ThreadError> threadErrors;
    XDebugSessionImpl session;
    String lastThreadId;

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

    synchronized public void onThreadError(ThreadErrorEvent threadErrorEvent) {
        VirtualFile file = threadErrorEvent.getFile();
        if(file == null) {
            return;
        }

        ThreadError threadError = new ThreadError(this.project,
                threadErrorEvent.getThreadId(),
                file,
                threadErrorEvent.getMsg(),
                threadErrorEvent.getLine());


            threadError.activate();
            this.threadErrors.put(threadErrorEvent.getThreadId(), threadError);
    }

    synchronized public void onThreadErrorClear(ClearThreadError clearThreadError) {
        ThreadError threadError = this.threadErrors.get(clearThreadError.getThreadId());

        if (threadError == null) {
            return;
        }

        threadError.deactivate();
        this.threadErrors.remove(clearThreadError.getThreadId());
    }

    synchronized public void onThreadChanged(String threadId) {
        if (this.lastThreadId != null && this.lastThreadId.equals(threadId)){
            return;
        }

        this.lastThreadId = threadId;

        ThreadError threadError = this.threadErrors.get(threadId);

        this.threadErrors.values().stream().filter(te -> !te.getThreadId().equals(threadId)).forEach(ThreadError::deactivate);

        if (threadError == null) {
            return;
        }

        if (!threadError.isActive()) {
            threadError.activate();
        }
    }

    public @Nullable String getActiveThread() {
        if (this.session == null) {
            return null;
        }

        XSuspendContext suspendContext = this.session.getSuspendContext();
        if (suspendContext == null) {
            return null;
        }

        PyExecutionStack executionStack = (PyExecutionStack) this.session.getSuspendContext().getActiveExecutionStack();
        assert executionStack != null;

        String threadId = executionStack.getThreadId();
        return threadId;
    }

    public @Nullable ThreadError getActiveError() {
        String activeThread = this.getActiveThread();
        if (activeThread == null) {
            return null;
        }

        ThreadError ret = this.threadErrors.get(activeThread);
        return ret;
    }

    @Override
    synchronized public void activate() {
        String activeThread = this.getActiveThread();
        if (activeThread == null) {
            return;
        }

        ThreadError threadError = this.threadErrors.get(activeThread);

        if (threadError == null) {
            return;
        }

        threadError.activate();
    }

    @Override
    synchronized public void deactivate() {
        this.threadErrors.values().forEach(ThreadError::deactivate);
    }
}
