package rw.session;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import rw.dialogs.DialogFactory;
import rw.dialogs.FirstUserError;
import rw.dialogs.TipDialog;
import rw.handler.runConf.PythonRunConfHandler;

public class UserError extends FileError {
    private static final Logger LOGGER = Logger.getInstance(UserError.class);

    public static final String ID = "UserError";

    UserError(Project project, PythonRunConfHandler handler, String[] args) {
        super(project, handler, args);
    }

    @Override
    public void handle() {
        LOGGER.info("Handling UserError " + String.format("(%s, %d)", this.file, this.line));
        super.handle();

        DialogFactory.get().showFirstUserErrorDialog(project);
    }
}
