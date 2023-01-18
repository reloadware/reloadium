package rw.action;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import rw.debugger.DebugRunner;
import rw.dialogs.DialogFactory;
import rw.dialogs.FirstDebugDialog;
import rw.dialogs.FirstRunDialog;
import rw.dialogs.TipDialog;
import rw.handler.runConf.BaseRunConfHandler;
import rw.handler.runConf.RunConfHandlerFactory;
import rw.icons.Icons;
import rw.service.Service;


public class DebugWithReloadium extends WithReloaderBase implements DumbAware {
    private static final Logger LOGGER = Logger.getInstance(DebugWithReloadium.class);

    public static String ID = "DebugWithReloadium";

    DebugWithReloadium() {
        super();
        this.runType = RunType.DEBUG;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = getEventProject(e);
        boolean result = DialogFactory.get().showFirstDebugDialog(project);

        Service.get().onRun();

        DialogFactory.get().showSurveyDialog(project);

        if (!result) {
            return;
        }

        super.actionPerformed(e);
    }

    @Override
    void setRunningIcon(AnActionEvent e) {
        e.getPresentation().setIcon(Icons.RestartDebugger);
    }
    void setNotRunningIcon(AnActionEvent e) {
        e.getPresentation().setIcon(Icons.Debug);
    }

    @Override
    protected Executor getExecutor() {
        return DefaultDebugExecutor.getDebugExecutorInstance();
    }
}
