package rw.dialogs;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;
import rw.media.Media;


public class FirstUserError extends TipDialog {
    private static final Logger LOGGER = Logger.getInstance(FirstUserError.class);

    public FirstUserError(@Nullable Project project) {
        super(project,
                "Fixing occurred errors",
                "Errors made during reloading can be easily fixed.<br>" +
                        "Simply correct your code and <b>save</b> your file while your application is <b>running</b>",
                Media.FixingUserErrors,
                false);
    }

    @Override
    protected boolean shouldBeShown() {
        return DialogsState.get().firstUserError;
    }

    @Override
    protected void onSetToBeShown(boolean value, int exitCode) {
        DialogsState.get().firstUserError = value;
    }
}
