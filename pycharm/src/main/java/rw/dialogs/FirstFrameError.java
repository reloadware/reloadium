package rw.dialogs;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;
import rw.media.Media;


public class FirstFrameError extends TipDialog {
    private static final Logger LOGGER = Logger.getInstance(FirstFrameError.class);

    public FirstFrameError(@Nullable Project project) {
        super(project,
                "Fixing frame errors",
                "Errors found during debugging can be fixed while in debug mode.<br>" +
                        "Simply correct your code and <b>save</b> your file to reload current function",
                Media.FixingFrameErrors,
                false);
    }

    @Override
    protected boolean shouldBeShown() {
        return DialogsState.get().firstFrameError;
    }

    @Override
    protected void onSetToBeShown(boolean value, int exitCode) {
        DialogsState.get().firstFrameError = value;
    }
}
