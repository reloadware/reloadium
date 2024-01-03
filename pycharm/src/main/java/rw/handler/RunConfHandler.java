package rw.handler;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.execution.Executor;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.execution.ui.RunContentWithExecutorListener;
import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.Content;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import rw.action.RunType;
import rw.debugger.FastDebug;
import rw.highlights.ErrorHighlightManager;
import rw.icons.IconPatcher;
import rw.icons.Icons;
import rw.profile.*;
import rw.quickconfig.ProfilerType;
import rw.quickconfig.QuickConfig;
import rw.quickconfig.QuickConfigCallback;
import rw.quickconfig.QuickConfigState;
import rw.service.Service;
import rw.session.Session;
import rw.session.cmds.QuickConfigCmd;
import rw.stack.Stack;
import rw.stack.ThreadErrorManager;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;

public abstract class RunConfHandler implements Disposable {
    private final Logger logger;

    AbstractPythonRunConfiguration<?> origRunConf;
    AbstractPythonRunConfiguration<?> runConf;
    RunnerAndConfigurationSettings runnerSettings;

    ExecutionEnvironment executionEnvironment;
    Stack stack;
    FrameProgressRenderer frameProgressRenderer;
    TimeProfiler timeProfiler;
    MemoryProfiler memoryProfiler;
    LineProfiler activeProfiler;
    NoneProfiler noneProfiler;
    FastDebug fastDebug;

    Session session;
    ErrorHighlightManager errorHighlightManager;
    ThreadErrorManager threadErrorManager;
    Project project;
    MessageBusConnection messageBusConnection;
    Set<File> watchedFiles;
    boolean active;
    @Nullable
    RunType runType;
    QuickConfig quickConfig;
    boolean firstActivate;
    Map<ProfilerType, LineProfiler> profilerTypeToProfiler;
    @Nullable ExtraEnvsSetter extraEnvsSetter;

    public RunConfHandler(RunConfiguration runConf) {
        this.logger = Logger.getInstance(this.getClass());

        this.origRunConf = (AbstractPythonRunConfiguration<?>) runConf;
        this.project = runConf.getProject();

        this.runConf = (AbstractPythonRunConfiguration<?>) this.origRunConf.clone();

        RunManager runManager = RunManager.getInstance(this.project);

        this.runnerSettings = runManager.createConfiguration(this.runConf, origRunConf.getFactory());
        this.runnerSettings.setTemporary(true);


        RunConfHandler This = this;
        this.quickConfig = new QuickConfig(new QuickConfigCallback() {
            @Override
            public void onChange(QuickConfigState state) {
                This.onQuickConfigChange(state);
            }
        });

        this.stack = new Stack(this.project, this);
        this.frameProgressRenderer = new FrameProgressRenderer(this.project);
        this.fastDebug = new FastDebug(this.project);

        this.timeProfiler = new TimeProfiler(this.project, this.quickConfig);
        this.memoryProfiler = new MemoryProfiler(this.project, this.quickConfig);
        this.noneProfiler = new NoneProfiler(this.project, this.quickConfig);

        this.session = new Session(this.project, this);
        this.errorHighlightManager = new ErrorHighlightManager(this.project);
        this.threadErrorManager = new ThreadErrorManager(this.project);

        this.watchedFiles = new HashSet<>();
        this.active = true;
        this.firstActivate = true;

        this.profilerTypeToProfiler = Map.ofEntries(
                entry(ProfilerType.TIME, this.timeProfiler),
                entry(ProfilerType.MEMORY, this.memoryProfiler),
                entry(ProfilerType.NONE, this.noneProfiler));

        this.activeProfiler = this.profilerTypeToProfiler.get(this.quickConfig.getState().getProfiler());

        this.handleJbEvents();
    }

    public AbstractPythonRunConfiguration<?> getRunConf() {
        return this.runConf;
    }
    public RunnerAndConfigurationSettings getRunnerSettings() {
        return this.runnerSettings;
    }


    public void setExtraEnvsSetter(ExtraEnvsSetter extraEnvsSetter) {
        this.extraEnvsSetter = extraEnvsSetter;
    }

    public void beforeRun(RunType runType) {
        RunConfHandlerManager.get().onBeforeRun(this);
    }

    public void onProcessStarted(RunContentDescriptor descriptor) {
        this.logger.info("On Process Started");
    }

    abstract public boolean isReloadiumActivated();

    abstract protected File getPackagesRootDir();

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
        RunConfHandlerManager.get().unregister(this.executionEnvironment);
    }

    public Stack getStack() {
        return stack;
    }

    public FastDebug getFastDebug() {
        return this.fastDebug;
    }

    public FrameProgressRenderer getStackRenderer() {
        return frameProgressRenderer;
    }

    public ThreadErrorManager getThreadErrorManager() {
        return this.threadErrorManager;
    }

    public ErrorHighlightManager getErrorHighlightManager() {
        return this.errorHighlightManager;
    }

    public Project getProject() {
        return project;
    }

    public boolean isActive() {
        return this.active;
    }

    private void handleJbEvents() {
        this.messageBusConnection = this.project.getMessageBus().connect(Service.get());
        RunConfHandler This = this;

        this.messageBusConnection.subscribe(RunContentManager.TOPIC, new RunContentWithExecutorListener() {
            @Override
            public void contentSelected(@Nullable RunContentDescriptor descriptor, @NotNull Executor executor) {
                if (This.getRunType() != RunType.DEBUG) {
                    return;
                }

                RunConfHandler handler = RunConfHandlerManager.get().getCurrentDebugHandler(project);

                if (handler == null) {
                    This.deactivate();
                    return;
                }

                if (handler == This) {
                    This.activate();
                } else {
                    This.deactivate();
                }
            }
        });
    }

    public void activate() {
        this.logger.info("Activating " + this.runConf.getName());
        if (this.activeProfiler != null) {
            this.activeProfiler.activate();
        }

        if (this.firstActivate) {
            this.onFirstActivate();
        }

        this.threadErrorManager.activate();
        this.fastDebug.activate();
        this.active = true;
        IconPatcher.refresh(this.getProject());
        this.refreshLineMarkers();
        this.firstActivate = false;
    }

    public @Nullable XDebugSessionImpl getDebugSession() {
        XDebugSessionImpl ret = ((XDebugSessionImpl) XDebuggerManager.getInstance(project).getCurrentSession());
        return ret;
    }

    public void onFirstActivate() {
        XDebugSessionImpl debugSession = this.getDebugSession();
        assert debugSession != null;
        this.threadErrorManager.onSessionStarted(debugSession);

        String id = Integer.toString(debugSession.hashCode());
        RunnerLayoutUi layout = RunnerLayoutUi.Factory.getInstance(this.project).create(id, "re_runner", "re_session",
                this);
        Content content = layout.createContent(id, quickConfig.getContent(), "loadium", Icons.ProductSmall, null);
        debugSession.getUI().addContent(content);

        debugSession.addSessionListener(new XDebugSessionListener() {
            @Override
            public void stackFrameChanged() {
                refreshLineMarkers();
            }
        });
    }

    private void refreshLineMarkers() {
        DaemonCodeAnalyzer.getInstance(project).restart();
    }

    public void deactivate() {
        this.logger.info("Deactivating " + this.runConf.getName());
        this.errorHighlightManager.deactivate();
        this.threadErrorManager.deactivate();
        this.activeProfiler.deactivate();
        this.fastDebug.deactivate();
        this.active = false;
        IconPatcher.refresh(this.getProject());
        this.refreshLineMarkers();
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

    @NotNull
    public Session getSession() {
        return this.session;
    }

    private void onQuickConfigChange(QuickConfigState state) {
        QuickConfigCmd cmd = new QuickConfigCmd(state);
        this.getSession().send(cmd, false);
        this.activeProfiler.deactivate();
        this.activeProfiler = this.profilerTypeToProfiler.get(state.getProfiler());
        this.activeProfiler.activate();
    }

    public TimeProfiler getTimeProfiler() {
        return this.timeProfiler;
    }

    public MemoryProfiler getMemoryProfiler() {
        return this.memoryProfiler;
    }
}
