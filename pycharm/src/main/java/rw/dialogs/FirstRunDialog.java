package rw.dialogs;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import rw.consts.Const;
import rw.media.Media;


public class FirstRunDialog extends TipDialog {
    private static final Logger LOGGER = Logger.getInstance(FirstRunDialog.class);

    public FirstRunDialog(@Nullable Project project) {
        super(project,
                String.format("Using %s in run mode", StringUtil.capitalize(Const.get().packageName)),
                "Simply edit and <b>save</b> your file while your application is <b>running</b>",
                Media.RunExample,
                true);
    }

    @Override
    protected boolean shouldBeShown() {
        return DialogsState.get().firstRun;
    }

    @Override
    protected void onSetToBeShown(boolean value, int exitCode) {
        DialogsState.get().firstRun = value;
    }
}
