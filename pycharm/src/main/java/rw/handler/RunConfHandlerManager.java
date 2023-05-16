package rw.handler;

import com.intellij.execution.RunManager;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import rw.pkg.PackageManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunConfHandlerManager {
    private static final Logger LOGGER = Logger.getInstance(RunConfHandlerManager.class);
    @VisibleForTesting

    public static RunConfHandlerManager singleton = null;
    @VisibleForTesting
    public PackageManager builtinPackageManager;
    Map<ExecutionEnvironment, RunConfHandler> all;
    @Nullable
    RunConfHandler last;

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

    @Nullable
    public RunConfHandler getRunConfHandler(long executionId) {
        return this.all.get(executionId);
    }

    public List<RunConfHandler> getAllActiveHandlers(@Nullable Project project) {
        List<RunConfHandler> ret = new ArrayList<>();

        this.all.forEach((key, value) -> {
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

        for (RunConfHandler h : this.all.values()) {
            if (h.runConf == configuration && h.isActive()) {
                return h;
            }
        }
        return null;
    }

    public void deactivateAll() {
        for (RunConfHandler runConfHandler : this.all.values()) {
            runConfHandler.deactivate();
        }
    }

}
