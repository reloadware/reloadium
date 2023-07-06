package rw.action;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;
import rw.handler.ExtraEnvsSetter;
import rw.handler.RunConfHandler;
import rw.handler.RunConfHandlerFactory;
import rw.icons.Icons;

import java.util.Map;


public class FastDebugWithReloadium extends DebugWithReloadium implements DumbAware {
    public static final String FASTDEBUG_WHOLEPROJECT_ENV = "RW_FASTDEBUG_WHOLEPROJECT";
    private static final Logger LOGGER = Logger.getInstance(FastDebugWithReloadium.class);
    public static String ID = "FastDebugWithReloadium";

    protected RunConfHandler handlerFactory(RunnerAndConfigurationSettings conf) {
        RunConfHandler ret = RunConfHandlerFactory.factory(conf.getConfiguration());
        FastDebugWithReloadium This = this;

        ret.setExtraEnvsSetter(new ExtraEnvsSetter() {
            @Override
            public void setEnvs(Map<String, String> envs) {
                envs.put(This.FASTDEBUG_WHOLEPROJECT_ENV, "True");
            }
        });
        return ret;
    }

    @Override
    void setRunningIcon(AnActionEvent e) {
        e.getPresentation().setIcon(Icons.RestartFastDebugger);
    }

    void setNotRunningIcon(AnActionEvent e) {
        e.getPresentation().setIcon(Icons.FastDebug);
    }

    protected String getEnabledText(@NotNull AnActionEvent e, RunnerAndConfigurationSettings conf) {
        String text = String.format("Fast %s", super.getEnabledText(e, conf));
        return text;
    }
}