package rw.handler.runConf;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.execution.ui.RunContentWithExecutorListener;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import rw.action.RunType;
import rw.profile.FrameProgressRenderer;
import rw.profile.LineProfiler;
import rw.stack.Stack;
import rw.handler.sdk.SdkHandler;
import rw.handler.sdk.SdkHandlerFactory;
import rw.highlights.ErrorHighlightManager;
import rw.profile.ProfilePreviewRenderer;
import rw.service.Service;
import rw.session.Session;

public abstract class BaseRunConfHandler implements Disposable {
    AbstractPythonRunConfiguration<?> runConf;
    @Nullable
    SdkHandler sdkHandler;
    ExecutionEnvironment executionEnvironment;
    Stack stack;
    FrameProgressRenderer frameProgressRenderer;
    LineProfiler lineProfiler;
    Session session;
    ErrorHighlightManager errorHighlightManager;
    ProfilePreviewRenderer profilePreviewRenderer;
    Project project;
    MessageBusConnection messageBusConnection;

    public BaseRunConfHandler(RunConfiguration runConf) {
        this.runConf = (AbstractPythonRunConfiguration<?>) runConf;
        this.project = this.runConf.getProject();

        this.sdkHandler = SdkHandlerFactory.factory(this.runConf.getSdk());
        this.stack = new Stack(this.project);
        this.frameProgressRenderer = new FrameProgressRenderer(this.project);
        this.lineProfiler = new LineProfiler();
        this.session = new Session(this.project, this);
        this.errorHighlightManager = new ErrorHighlightManager(this.project);
        this.profilePreviewRenderer = new ProfilePreviewRenderer(this.project, this.stack, this.lineProfiler);

        this.handleJbEvents();
    }

    abstract public void beforeRun(RunType runType);

    abstract public void onProcessStarted(RunContentDescriptor descriptor);

    abstract public void afterRun();

    abstract public boolean isReloadiumActivated();

    public @NotNull
    String convertPathToLocal(@NotNull String remotePath) {
        return remotePath;
    }

    public @NotNull
    String convertPathToRemote(@NotNull String localPath) {
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

    private void handleJbEvents() {
        this.messageBusConnection = this.project.getMessageBus().connect(Service.get());

        BaseRunConfHandler This = this;

        this.messageBusConnection.subscribe(RunContentManager.TOPIC, new RunContentWithExecutorListener() {
            @Override
            public void contentSelected(@Nullable RunContentDescriptor descriptor, @NotNull Executor executor) {
                BaseRunConfHandler handler = RunConfHandlerManager.get().getCurrentHandler(project);
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
        this.profilePreviewRenderer.activate();
        this.frameProgressRenderer.activate();
    }

    public void deactivate() {
        this.errorHighlightManager.deactivate();
        this.profilePreviewRenderer.deactivate();
        this.frameProgressRenderer.deactivate();
    }

    @Override
    public void dispose() {
        this.messageBusConnection.dispose();
    }

    public ProfilePreviewRenderer getProfilePreviewRenderer() {
        return profilePreviewRenderer;
    }


    // Test methods ################
    @VisibleForTesting
    public void __setErrorHighlightManager(ErrorHighlightManager errorHighlightManager) {
        this.errorHighlightManager = errorHighlightManager;
    }

    public LineProfiler getTimeProfiler() {
        return this.lineProfiler;
    }

    public Session getSession() {
        return this.session;
    }
}
