package rw.handler.runConf;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.execution.ui.RunContentWithExecutorListener;
import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.Content;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import rw.action.RunType;
import rw.icons.IconPatcher;
import rw.icons.Icons;
import rw.profile.*;
import rw.quickconfig.ProfilerType;
import rw.quickconfig.QuickConfig;
import rw.quickconfig.QuickConfigCallback;
import rw.quickconfig.QuickConfigState;
import rw.session.cmds.QuickConfigCmd;
import rw.session.events.Event;
import rw.session.events.Handshake;
import rw.session.events.ModuleUpdate;
import rw.stack.Stack;
import rw.handler.sdk.SdkHandler;
import rw.handler.sdk.SdkHandlerFactory;
import rw.highlights.ErrorHighlightManager;
import rw.service.Service;
import rw.session.Session;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;

public abstract class BaseRunConfHandler implements Disposable {
    AbstractPythonRunConfiguration<?> runConf;
    @Nullable
    SdkHandler sdkHandler;
    ExecutionEnvironment executionEnvironment;
    Stack stack;
    FrameProgressRenderer frameProgressRenderer;
    TimeProfiler timeProfiler;
    MemoryProfiler memoryProfiler;
    LineProfiler activeProfiler;

    Session session;
    ErrorHighlightManager errorHighlightManager;
    Project project;
    MessageBusConnection messageBusConnection;
    Set<File> watchedFiles;
    boolean active;
    @Nullable
    RunType runType;
    QuickConfig quickConfig;
    boolean firstActivate;
    Map<ProfilerType, LineProfiler> profilerTypeToProfiler;

    public BaseRunConfHandler(RunConfiguration runConf) {
        this.runConf = (AbstractPythonRunConfiguration<?>) runConf;
        this.project = this.runConf.getProject();

        BaseRunConfHandler This = this;
        this.quickConfig = new QuickConfig(new QuickConfigCallback() {
            @Override
            public void onChange(QuickConfigState state) {
                This.onQuickConfigChange(state);
            }
        });

        this.sdkHandler = SdkHandlerFactory.factory(this.runConf.getSdk());
        this.stack = new Stack(this.project);
        this.frameProgressRenderer = new FrameProgressRenderer(this.project);
        this.timeProfiler = new TimeProfiler(this.project, this.quickConfig);

        this.memoryProfiler = new MemoryProfiler(this.project, this.quickConfig);
        this.session = new Session(this.project, this);
        this.errorHighlightManager = new ErrorHighlightManager(this.project);

        this.watchedFiles = new HashSet<>();
        this.active = true;
        this.firstActivate = true;

        this.profilerTypeToProfiler = Map.ofEntries(
                entry(ProfilerType.TIME, this.timeProfiler),
                entry(ProfilerType.MEMORY, this.memoryProfiler));

        this.activeProfiler = this.profilerTypeToProfiler.get(this.quickConfig.getState().getProfiler());

        this.handleJbEvents();
    }

    public void beforeRun(RunType runType) {
    }

    public void onProcessStarted(RunContentDescriptor descriptor) {
    }

    abstract public void afterRun();

    abstract public boolean isReloadiumActivated();

    public RunType getRunType() {
        return this.runType;
    }

    public @NotNull
    String convertPathToLocal(@NotNull String remotePath, boolean warnMissing) {
        return remotePath;
    }

    public @NotNull
    String convertPathToRemote(@NotNull String localPath, boolean warnMissing) {
        return localPath;
    }

    public String getWorkingDirectory() {
        return this.runConf.getWorkingDirectory();
    }

    public void setExecutionEnvironment(ExecutionEnvironment executionEnvironment) {
        this.executionEnvironment = executionEnvironment;
    }

    public void onProcessExit() {
        this.deactivate();
    }

    public Stack getStack() {
        return stack;
    }

    public FrameProgressRenderer getStackRenderer() {
        return frameProgressRenderer;
    }

    public ErrorHighlightManager getErrorHighlightManager() {
        return errorHighlightManager;
    }

    public Project getProject() {
        return project;
    }

    public boolean isActive() {
        return this.active;
    }

    private void handleJbEvents() {
        this.messageBusConnection = this.project.getMessageBus().connect(Service.get());
        BaseRunConfHandler This = this;

        this.messageBusConnection.subscribe(RunContentManager.TOPIC, new RunContentWithExecutorListener() {
            @Override
            public void contentSelected(@Nullable RunContentDescriptor descriptor, @NotNull Executor executor) {
                if (This.getRunType() != RunType.DEBUG) {
                    return;
                }

                BaseRunConfHandler handler = RunConfHandlerManager.get().getCurrentHandler(project);

                if (handler == null) {
                    return;
                }

                if (handler == This) {
                    This.activate();
                }
                else {
                    This.deactivate();
                }
            }
        });
    }

    public void activate() {
        this.errorHighlightManager.activate();
        this.activeProfiler.activate();
        this.frameProgressRenderer.activate();
        this.active = true;
        IconPatcher.refresh(this.getProject());

        if (this.firstActivate) {
            this.onFirstActivate();
        }
        this.firstActivate = false;
    }

    public @Nullable XDebugSessionImpl getDebugSession() {
        XDebugSessionImpl ret = ((XDebugSessionImpl) XDebuggerManager.getInstance(project).getCurrentSession());
        return ret;
    }

    public void onFirstActivate() {
        XDebugSessionImpl debugSession = this.getDebugSession();
        assert debugSession != null;
        String id = Integer.toString(debugSession.hashCode());
        RunnerLayoutUi layout = RunnerLayoutUi.Factory.getInstance(this.project).create(id, "re_runner", "re_session",
                this);
        Content content = layout.createContent(id, quickConfig.getContent(), "loadium", Icons.ProductIcon, null);
        debugSession.getUI().addContent(content);
    }

    public void deactivate() {
        this.errorHighlightManager.deactivate();
        this.activeProfiler.deactivate();
        this.frameProgressRenderer.deactivate();
        this.active = false;
        IconPatcher.refresh(this.getProject());
    }

    public QuickConfig getQuickConfig() {
        return this.quickConfig;
    }

    public void addWatched(Set<File> files) {
        this.watchedFiles.addAll(files);
    }

    public boolean isWatched(File file) {
        boolean ret = this.watchedFiles.contains(file);
        return ret;
    }

    @Override
    public void dispose() {
        this.messageBusConnection.dispose();
    }

    // Test methods ################
    @VisibleForTesting
    public void __setErrorHighlightManager(ErrorHighlightManager errorHighlightManager) {
        this.errorHighlightManager = errorHighlightManager;
    }

    public LineProfiler getActiveProfiler() {
        return this.activeProfiler;
    }

    @NotNull public Session getSession() {
        return this.session;
    }

    private void onQuickConfigChange(QuickConfigState state) {
        QuickConfigCmd cmd = new QuickConfigCmd(state);
        this.getSession().send(cmd);
        if (this.activeProfiler != null) {
            this.activeProfiler.deactivate();
        }
        this.activeProfiler = this.profilerTypeToProfiler.getOrDefault(state.getProfiler(), null);

        if (this.activeProfiler != null) {
            this.activeProfiler.activate();
        }
    }

    public TimeProfiler getTimeProfiler() {
        return this.timeProfiler;
    }

    public MemoryProfiler getMemoryProfiler() {
        return this.memoryProfiler;
    }
}
