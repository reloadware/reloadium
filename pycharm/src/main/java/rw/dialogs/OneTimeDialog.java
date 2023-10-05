package rw.dialogs;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;



public abstract class OneTimeDialog extends DialogWrapper {
    private static final Logger LOGGER = Logger.getInstance(OneTimeDialog.class);

    public OneTimeDialog(@Nullable Project project) {
        super(project, false);
        this.init();
    }

    @Override
    public void show() {
        if (!this.shouldBeShown()) {
            return;
        }
        super.show();
        this.setShown();
    }

    protected abstract boolean shouldBeShown();

    protected abstract void setShown();
}
