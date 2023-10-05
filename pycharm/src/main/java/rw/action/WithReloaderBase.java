package rw.action;

import com.intellij.execution.*;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.impl.ExecutionManagerImpl;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.ide.ui.IdeUiService;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.audit.RwSentry;
import rw.consts.Const;
import rw.debugger.DebugRunner;
import rw.handler.RunConfHandler;
import rw.handler.RunConfHandlerFactory;
import rw.handler.RunConfHandlerManager;
import rw.icons.Icons;
import rw.util.Vendored;

import java.lang.reflect.Field;


public abstract class WithReloaderBase extends AnAction {
    private static final Logger LOGGER = Logger.getInstance(WithReloaderBase.class);
    RunType runType;

    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setVisible(true);

        Project project = getEventProject(e);

        if (project == null)
            return;

        if (!this.canRun(e)) {
            presentation.setEnabled(false);
        } else {
            RunnerAndConfigurationSettings conf = this.getConfiguration(e);
            if(conf == null) {
                return;
            }
            this.handleRunningConfs(project, e, conf);
            this.setEnabledText(e, conf);
            presentation.setEnabled(true);
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    public boolean canRun(@NotNull AnActionEvent e) {
        RunnerAndConfigurationSettings conf = this.getConfiguration(e);

        if (conf == null) {
            return false;
        }
        boolean ret = AbstractPythonRunConfiguration.class.isAssignableFrom(conf.getConfiguration().getClass());
        return ret;
    }

    protected String getEnabledText(@NotNull AnActionEvent e, RunnerAndConfigurationSettings conf) {
        String text = String.format("%s '%s' with %s", this.getExecutor().getActionName(),
                conf.getName(),
                StringUtil.capitalize(Const.get().packageName));
        text = StringUtil.escapeMnemonics(text);
        return text;
    }

    public void setEnabledText(@NotNull AnActionEvent e, RunnerAndConfigurationSettings conf) {
        e.getPresentation().setText(this.getEnabledText(e, conf));
    }

    public void restartRunProfile(@NotNull Project project, ExecutionEnvironment environment) {
        ExecutionManager.getInstance(project).restartRunProfile(environment);
    }

    public void start(@NotNull Project project, RunnerAndConfigurationSettings conf) {
        try {
            ExecutionEnvironment environment = this.getEnvironment(project, conf);

            if (ExecutionManager.getInstance(project).isStarting(environment))
                return;

            this.restartRunProfile(project, environment);
        } catch (Exception exception) {
            RwSentry.get().captureException(exception, true);
        }
    }

    public void actionPerformed(@NotNull AnActionEvent e) {
        LOGGER.info("Performing action");
        Project project = getEventProject(e);
        if (project == null) {
            return;
        }
        RunnerAndConfigurationSettings runConf = this.getConfiguration(e);
        assert runConf != null;
        RunManagerImpl runManager = RunManagerImpl.getInstanceImpl(project);

        if (!runManager.hasSettings(runConf)) {
            runManager.addConfiguration(runConf);
        }

        if (runManager.getSelectedConfiguration() != runConf) {
            runManager.setSelectedConfiguration(runConf);
        }

        AbstractPythonRunConfiguration<?> pythonRunConf = (AbstractPythonRunConfiguration<?>) runConf.getConfiguration();
        Sdk sdk = pythonRunConf.getSdk();
        if (sdk == null) {
            IdeUiService.getInstance().notifyByBalloon(project, this.getExecutor().getToolWindowId(),
                    MessageType.ERROR, ExecutionBundle.message("error.running.configuration.message", runConf.getName()) +
                            "\nSDK is not defined for Run Configuration",
                    Icons.ProductSmall, null);
            return;
        }

        this.start(project, runConf);
    }

    protected RunConfHandler handlerFactory(RunnerAndConfigurationSettings conf) {
        RunConfHandler ret = RunConfHandlerFactory.factory(conf.getConfiguration());
        return ret;
    }

    public ExecutionEnvironment getEnvironment(@NotNull Project project, @NotNull RunnerAndConfigurationSettings conf) throws ExecutionException {
        RunConfHandler handler = this.handlerFactory(conf);
        Executor executor = this.getExecutor();

        ExecutionEnvironmentBuilder builder = ExecutionEnvironmentBuilder.create(executor, handler.getRunConf());

        if (executor.getClass().isAssignableFrom(DefaultDebugExecutor.class)) {
            DebugRunner runner = new DebugRunner(handler);
            builder = builder.runner(runner);
        }

        ExecutionEnvironment ret = builder.build(descriptor -> {
            handler.onProcessStarted(descriptor);
            descriptor.getProcessHandler().addProcessListener(new ProcessAdapter() {
                @Override
                public void processTerminated(@NotNull ProcessEvent event) {
                    handler.onProcessExit();
                }
            });
        });

        Field runnerAndConfigurationSettingsField;
        try {
            runnerAndConfigurationSettingsField = ret.getClass().getDeclaredField("myRunnerAndConfigurationSettings");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        runnerAndConfigurationSettingsField.setAccessible(true);
        try {
            runnerAndConfigurationSettingsField.set(ret, conf);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        handler.setExecutionEnvironment(ret);
        RunConfHandlerManager.get().register(ret, handler);

        handler.beforeRun(this.runType);
        return ret;
    }

    @Nullable
    protected RunnerAndConfigurationSettings getSelectedConfiguration(@NotNull AnActionEvent e) {
        Project project = getEventProject(e);
        RunManagerImpl runManager = RunManagerImpl.getInstanceImpl(project);
        RunnerAndConfigurationSettings ret = runManager.getSelectedConfiguration();
        return ret;
    }

    @Nullable
    protected RunnerAndConfigurationSettings getConfiguration(@NotNull AnActionEvent e) {
        Project project = getEventProject(e);
        RunnerAndConfigurationSettings conf = this.getSelectedConfiguration(e);

        if(conf != null) {
            return conf;
        }

        VirtualFile[] files = FileEditorManager.getInstance(project).getSelectedFiles();

        if (files.length == 0) {
            return null;
        }

         PsiFile psiFile = PsiManager.getInstance(project).findFile(files[0]);

        if (psiFile == null) {
            return null;
        }
        RunnerAndConfigurationSettings ret = Vendored.getRunConfigForCurrentFile(psiFile);
        return ret;
    }

    private void handleRunningConfs(Project project, @NotNull AnActionEvent e, RunnerAndConfigurationSettings conf) {
        ExecutionManagerImpl executionManager = ExecutionManagerImpl.getInstance(project);

        Condition condition = object -> true;

        boolean isRunning = false;

        for (Object p : executionManager.getRunningDescriptors(condition)) {
            RunContentDescriptor descr = (RunContentDescriptor) p;
            if (conf.getName().equals(descr.getDisplayName()) && executionManager.getExecutors(descr).contains(this.getExecutor())) {
                isRunning = true;
            }
        }

        if (isRunning) {
            this.setRunningIcon(e);
        } else {
            this.setNotRunningIcon(e);
        }
    }

    public abstract Executor getExecutor();

    abstract void setRunningIcon(AnActionEvent e);

    abstract void setNotRunningIcon(AnActionEvent e);
}