package rw.audit;

import com.intellij.diagnostic.AbstractMessage;
import com.intellij.diagnostic.IdeaReportingEvent;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.consts.Const;

import java.awt.*;


public class ErrorSubmitter extends ErrorReportSubmitter {
    @Nullable
    @Override
    public String getPrivacyNoticeText() {
        return String.format("Hereby you agree to <a href=\"%s\">this privacy policy</a>",
                Const.get().privacyPolicyUrl);
    }

    @NotNull
    @Override
    public String getReportActionText() {
        return "Report to Author";
    }

    @Override
    public boolean submit(@NotNull IdeaLoggingEvent[] events,
                          @Nullable String additionalInfo,
                          @NotNull Component parentComponent,
                          @NotNull Consumer<? super SubmittedReportInfo> consumer) {
        DataContext context = DataManager.getInstance().getDataContext(parentComponent);
        Project project = CommonDataKeys.PROJECT.getData(context);

        new Task.Backgroundable(project, "Sending Error Report") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                for (IdeaLoggingEvent ideaEvent : events) {
                    if (!(ideaEvent instanceof IdeaReportingEvent && ideaEvent.getData() instanceof AbstractMessage)) {
                        continue;
                    }
                    Throwable ex = ((AbstractMessage) ideaEvent.getData()).getThrowable();
                    RwSentry.get().submitException(ex);
                }

                ApplicationManager.getApplication().invokeLater(() -> {
                    Messages.showInfoMessage(parentComponent, "Thank you for submitting your report!", "Error Report");
                    consumer.consume(new SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE));
                });
            }
        }.queue();
        return true;
    }
}