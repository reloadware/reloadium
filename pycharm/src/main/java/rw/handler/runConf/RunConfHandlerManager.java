package rw.handler.runConf;

import com.intellij.concurrency.JobScheduler;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import com.jetbrains.python.sdk.PythonSdkUtil;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import rw.audit.RwSentry;
import rw.consts.Const;
import rw.pkg.BuiltinPackageManager;
import rw.service.Service;
import rw.util.OsType;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class RunConfHandlerManager {
    private static final Logger LOGGER = Logger.getInstance(RunConfHandlerManager.class);
    @VisibleForTesting
    public BuiltinPackageManager builtinPackageManager;
    @VisibleForTesting

    public static RunConfHandlerManager singleton = null;

    Map<ExecutionEnvironment, BaseRunConfHandler> all;
    @Nullable
    BaseRunConfHandler last;

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

    public void register(ExecutionEnvironment executionEnvironment, BaseRunConfHandler baseRunConfHandler) {
        this.all.put(executionEnvironment, baseRunConfHandler);
        this.last = baseRunConfHandler;
    }

    @Nullable
    public BaseRunConfHandler getRunConfHandler(long executionId) {
        return this.all.get(executionId);
    }

    public List<BaseRunConfHandler> getForProject(Project project) {
        List<BaseRunConfHandler> ret = new ArrayList<>();

        this.all.forEach((key, value) -> {
            if(value.getProject() == project) {
                ret.add(value);
            }
        });
        return ret;
    }

    public List<BaseRunConfHandler> getAllActiveHandlers(@Nullable Project project) {
        List<BaseRunConfHandler> ret = new ArrayList<>();

        this.all.forEach((key, value) -> {
            if((project == null || value.getProject() == project) && value.isActive()) {
                ret.add(value);
            }
        });
        return ret;
    }

    @Nullable
    public BaseRunConfHandler getCurrentHandler(Project project) {
        XDebugSessionImpl debugSession = ((XDebugSessionImpl) XDebuggerManager.getInstance(project).getCurrentSession());

        if (debugSession == null) {
            return null;
        }

        ExecutionEnvironment environment = debugSession.getExecutionEnvironment();

        if (environment == null) {
            return null;
        }

        BaseRunConfHandler handler = this.all.get(environment);
        return handler;
    }

    @Nullable
    public BaseRunConfHandler getLastHandler() {
        return this.last;
    }

    public void deactivateAll() {
        for(BaseRunConfHandler runConfHandler: this.all.values()) {
            runConfHandler.deactivate();
        }
    }

}
