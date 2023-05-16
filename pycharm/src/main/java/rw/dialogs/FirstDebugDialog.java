package rw.dialogs;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import rw.consts.Const;
import rw.media.Media;


public class FirstDebugDialog extends TipDialog {
    private static final Logger LOGGER = Logger.getInstance(FirstDebugDialog.class);

    public FirstDebugDialog(@Nullable Project project) {
        super(project,
                String.format("Using %s in debug mode", StringUtil.capitalize(Const.get().packageName)),
                "Same behaviour from run mode applies to debug mode.<br>" +
                        "To observe dynamic code changes please <b>save</b> the modified file<br>" +
                        "On top of that in debug mode it is possible to reload current function like shown above.",
                Media.DebugExample,
                true);
    }

    @Override
    protected boolean shouldBeShown() {
        return DialogsState.get().firstDebug;
    }

    @Override
    protected void onSetToBeShown(boolean value, int exitCode) {
        DialogsState.get().firstDebug = value;
    }
}
