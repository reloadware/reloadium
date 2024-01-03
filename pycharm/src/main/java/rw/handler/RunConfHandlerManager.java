package rw.handler;

import com.intellij.execution.RunManager;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunConfHandlerManager {
    private static final Logger LOGGER = Logger.getInstance(RunConfHandlerManager.class);
    @VisibleForTesting

    public static RunConfHandlerManager singleton = null;
    Map<ExecutionEnvironment, RunConfHandler> all;
    @Nullable
    RunConfHandler last;
    private List<RunListener> listeners = new ArrayList<>();

    @VisibleForTesting
    public RunConfHandlerManager() {
        this.all = new HashMap<>();
        this.last = null;
    }

    public static RunConfHandlerManager get() {
        if (singleton == null) {
            singleton = new RunConfHandlerManager();
        }

        return singleton;
    }

    public void register(ExecutionEnvironment executionEnvironment, RunConfHandler runConfHandler) {
        this.all.put(executionEnvironment, runConfHandler);
        this.last = runConfHandler;
    }

    public void unregister(ExecutionEnvironment executionEnvironment) {
        this.all.remove(executionEnvironment);
    }

    public List<RunConfHandler> getAllActiveHandlers(@Nullable Project project) {
        List<RunConfHandler> ret = new ArrayList<>();

        Map<ExecutionEnvironment, RunConfHandler> allCopy = new HashMap<>(this.all);

        allCopy.forEach((key, value) -> {
            if ((project == null || value.getProject() == project) && value.isActive()) {
                ret.add(value);
            }
        });
        return ret;
    }

    @Nullable
    public RunConfHandler getCurrentDebugHandler(Project project) {
        XDebugSessionImpl debugSession = ((XDebugSessionImpl) XDebuggerManager.getInstance(project).getCurrentSession());

        if (debugSession == null) {
            return null;
        }

        ExecutionEnvironment environment = debugSession.getExecutionEnvironment();

        if (environment == null) {
            return null;
        }

        RunConfHandler handler = this.all.get(environment);
        return handler;
    }

    @Nullable
    public RunConfHandler getCurrentRunHandler(Project project) {
        RunManager runManager = RunManager.getInstance(project);

        if (runManager.getSelectedConfiguration() == null) {
            return null;
        }

        RunConfiguration configuration = runManager.getSelectedConfiguration().getConfiguration();

        Map<ExecutionEnvironment, RunConfHandler> allCopy = new HashMap<>(this.all);

        for (RunConfHandler h : allCopy.values()) {
            if (h.runConf == configuration && h.isActive()) {
                return h;
            }
        }
        return null;
    }

    public void addListener(@NotNull RunListener listener) {
        this.listeners.add(listener);
    }

    public void onBeforeRun(@NotNull RunConfHandler handler) {
        for (RunListener listener : this.listeners) {
            listener.onBeforeRun(handler);
        }
    }
}
