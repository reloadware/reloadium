package rw.dialogs;

import com.google.common.annotations.VisibleForTesting;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;


public class DialogFactory {
    private static final Logger LOGGER = Logger.getInstance(DialogFactory.class);

    @VisibleForTesting
    public static DialogFactory singleton;

    public static DialogFactory get() {
        if (singleton == null) {
            singleton = new DialogFactory();
        }
        return singleton;
    }

    public boolean showFirstRunDialog(Project project) {
        FirstRunDialog dialog = new FirstRunDialog(project);
        dialog.show();
        return dialog.getResult();
    }

    public boolean showFirstDebugDialog(Project project) {
        FirstDebugDialog dialog = new FirstDebugDialog(project);
        dialog.show();
        return dialog.getResult();
    }

    public void showFirstUserErrorDialog(Project project) {
        ApplicationManager.getApplication().invokeLater(() -> {
            FirstUserError dialog = new FirstUserError(project);
            dialog.show();
        });
    }

    public void showFirstThreadErrorDialog(Project project) {
        ApplicationManager.getApplication().invokeLater(() -> {
            FirstFrameError dialog = new FirstFrameError(project);
            dialog.show();
        });
    }
}