package rw.action;

import com.intellij.execution.*;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.impl.ExecutionManagerImpl;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.ide.ui.IdeUiService;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.audit.RwSentry;
import rw.consts.Const;
import rw.debugger.DebugRunner;
import rw.handler.BaseRunConfHandler;
import rw.handler.RunConfHandlerFactory;
import rw.handler.RunConfHandlerManager;
import rw.icons.Icons;

import java.util.TimerTask;

import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import rw.service.Service;


public abstract class WithReloaderBase extends AnAction {
    RunType runType;

    private static final Logger LOGGER = Logger.getInstance(WithReloaderBase.class);

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
            assert conf != null;
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

    public void start(@NotNull Project project, RunnerAndConfigurationSettings conf) {
        try {
            RunManagerImpl runManager = RunManagerImpl.getInstanceImpl(project);
            ExecutionEnvironment environment = this.getEnvironment(conf);

            if (!runManager.hasSettings(conf)) {
                runManager.addConfiguration(conf);
            }
            runManager.setSelectedConfiguration(conf);

            if (ExecutionManager.getInstance(project).isStarting(environment))
                return;

            ExecutionManager.getInstance(project).restartRunProfile(environment);
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

        AbstractPythonRunConfiguration<?> pythonRunConf = (AbstractPythonRunConfiguration<?>) runConf.getConfiguration();
        Sdk sdk = pythonRunConf.getSdk();

        if(sdk == null) {
            IdeUiService.getInstance().notifyByBalloon(project, this.getExecutor().getToolWindowId(),
                    MessageType.ERROR, ExecutionBundle.message("error.running.configuration.message", runConf.getName()) +
                    "\nSDK is not defined for Run Configuration",
                    Icons.ProductIcon, null);
            return;
        }

        this.start(project, runConf);
    }

    protected BaseRunConfHandler handlerFactory(RunnerAndConfigurationSettings conf) {
        BaseRunConfHandler ret = RunConfHandlerFactory.factory(conf.getConfiguration());
        return ret;
    }

    protected ExecutionEnvironment getEnvironment(RunnerAndConfigurationSettings conf) throws ExecutionException {
        BaseRunConfHandler handler = this.handlerFactory(conf);
        Executor executor = this.getExecutor();

        ExecutionEnvironmentBuilder builder = ExecutionEnvironmentBuilder.create(executor, conf);

        if(executor.getClass().isAssignableFrom(DefaultDebugExecutor.class)) {
            DebugRunner runner = new DebugRunner(handler);
            builder = builder.runner(runner);
        }

        TimerTask task = new TimerTask() {
            public void run() {
            if (handler.isReloadiumActivated())
                handler.afterRun();
            }
        };

        new java.util.Timer().schedule(task, 5000);

        ExecutionEnvironment ret = builder.build(new ProgramRunner.Callback() {
            @Override
            public void processStarted(RunContentDescriptor descriptor) {
                handler.onProcessStarted(descriptor);
                handler.afterRun();
                descriptor.getProcessHandler().addProcessListener(new ProcessAdapter() {
                    @Override
                    public void processTerminated(@NotNull ProcessEvent event) {
                        handler.onProcessExit();
                    }
                });
            }
        });

        handler.setExecutionEnvironment(ret);
        RunConfHandlerManager.get().register(ret, handler);

        handler.beforeRun(this.runType);
        return ret;
    }

    @Nullable
    protected RunnerAndConfigurationSettings getConfiguration(Project project) {
        RunManagerImpl runManager = RunManagerImpl.getInstanceImpl(project);
        RunnerAndConfigurationSettings conf = runManager.getSelectedConfiguration();
        return conf;
    }

    @Nullable
    protected RunnerAndConfigurationSettings getConfiguration(@NotNull AnActionEvent e) {
        Project project = getEventProject(e);
        return this.getConfiguration(project);
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