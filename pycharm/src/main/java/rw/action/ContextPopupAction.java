package rw.action;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionUpdateThreadAware;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;


abstract public class ContextPopupAction extends WithReloaderBase implements ActionUpdateThreadAware {
    private static final Logger LOGGER = Logger.getInstance(ContextPopupAction.class);

    public void update(@NotNull AnActionEvent e) {
        super.update(e);
    }

    protected RunnerAndConfigurationSettings getSelectedConfiguration(@NotNull AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();
        final ConfigurationContext context = ConfigurationContext.getFromContext(dataContext, ActionPlaces.UNKNOWN);

        RunnerAndConfigurationSettings ret = null;
        try {
            ret = context.findExisting();
        } catch (Exception exception) {
            LOGGER.warn("Failed to get selected configuration", exception);
        }

        if (ret == null) {
            ret = context.getConfiguration();
        }
        return ret;
    }
}
