package rw.action;

import com.intellij.execution.Executor;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import rw.handler.ExtraEnvsSetter;
import rw.handler.RunConfHandler;
import rw.handler.RunConfHandlerFactory;
import rw.icons.Icons;

import java.util.Map;


public class FastDebugWithReloadiumRunContext extends ContextPopupAction {

    public static String ID = "FastDebugWithReloadiumRunContextPopup";

    FastDebugWithReloadiumRunContext() {
        super();
        this.runType = RunType.DEBUG;
    }

    @Override
    void setRunningIcon(AnActionEvent e) {
        e.getPresentation().setIcon(Icons.RestartFastDebugger);
    }

    @Override
    void setNotRunningIcon(AnActionEvent e) {
        e.getPresentation().setIcon(Icons.FastDebug);
    }

    @Override
    public Executor getExecutor() {
        return DefaultDebugExecutor.getDebugExecutorInstance();
    }

    protected RunConfHandler handlerFactory(RunnerAndConfigurationSettings conf) {
        RunConfHandler ret = RunConfHandlerFactory.factory(conf.getConfiguration());

        ret.setExtraEnvsSetter(new ExtraEnvsSetter() {
            @Override
            public void setEnvs(Map<String, String> envs) {
                envs.put(FastDebugWithReloadium.FASTDEBUG_WHOLEPROJECT_ENV, "True");
            }
        });
        return ret;
    }

    protected String getEnabledText(@NotNull AnActionEvent e, RunnerAndConfigurationSettings conf) {
        String text = String.format("Fast %s", super.getEnabledText(e, conf));
        return text;
    }
}
