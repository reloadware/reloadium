package rw.dialogs;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import rw.consts.Const;
import rw.media.Media;
import rw.service.Service;

import javax.swing.*;
import java.util.List;


public class SurveyDialog extends TipDialog {
    private static final Logger LOGGER = Logger.getInstance(SurveyDialog.class);
    private static List<Integer> onRuns = List.of(20, 50, 100);

    public SurveyDialog(@Nullable Project project) {
        super(project,
                String.format("Help us improve Reloadium", StringUtil.capitalize(Const.get().packageName)),
                "Help us improve Reloadium by filling our survey<br>" +
                        "<center><a href=\"https://www.surveymonkey.com/r/69CCTXZ\">www.surveymonkey.com/r/69CCTXZ</a></center>",
                Media.HelpNeeded,
                false);
    }

    @Override
    protected boolean shouldBeShown() {
        boolean ret = DialogsState.get().survey && onRuns.contains(Service.get().getRunCounter());
        return ret;
    }

    @Override
    protected void onSetToBeShown(boolean value, int exitCode) {
        DialogsState.get().survey = value;
    }
}
