package rw.action;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.UpdateInBackground;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;


abstract public class ContextPopupAction extends WithReloaderBase implements UpdateInBackground {
    private static final Logger LOGGER = Logger.getInstance(ContextPopupAction.class);

    public void update(@NotNull AnActionEvent e) {
        if(this.canRun(e)) {
            super.update(e);
        }
        else {
            Presentation presentation = e.getPresentation();
            presentation.setVisible(false);
        }
    }

    protected RunnerAndConfigurationSettings getConfiguration(@NotNull AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();
        final ConfigurationContext context = ConfigurationContext.getFromContext(dataContext);

        RunnerAndConfigurationSettings ret = context.findExisting();

        if (ret == null) {
            ret = context.getConfiguration();
        }
        return ret;
    }
}
