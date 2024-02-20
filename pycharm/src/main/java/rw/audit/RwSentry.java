package rw.audit;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.protocol.Message;
import io.sentry.protocol.User;
import org.jetbrains.annotations.VisibleForTesting;
import rw.config.Config;
import rw.config.ConfigManager;
import rw.consts.Const;
import rw.consts.Stage;

import java.util.Arrays;


public class RwSentry {
    @VisibleForTesting
    public static RwSentry singleton = null;

    private RwSentry() {
    }

    public static RwSentry get() {
        if (singleton == null)
            singleton = new RwSentry();

        return singleton;
    }

    public void enable() {
        Sentry.init(options -> {
            options.setDsn(Const.get().sentryDsn);
            options.setEnvironment(Const.get().stage.value);
        });
    }

    public void disable() {
        Sentry.init("");
    }

    public void captureError(String msg) {
        this.enable();

        SentryEvent event = new SentryEvent();
        if (msg != null) {
            Message message = new Message();
            message.setMessage(msg);
            event.setMessage(message);
        }

        this.addExtraInfo(event);
        Sentry.captureEvent(event);

        this.disable();
    }

    public void captureException(Throwable throwable, boolean fail) {
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            throw new RuntimeException(throwable);
        }

        if (Arrays.asList(Stage.LOCAL, Stage.CI).contains(Const.get().stage)) {
            throwable.printStackTrace();
        }

        this.enable();

        SentryEvent event = new SentryEvent();

        event.setThrowable(throwable);
        this.addExtraInfo(event);
        Sentry.captureEvent(event);

        this.disable();

        if (fail) {
            throw new RuntimeException(throwable);
        }
    }

    public void submitException(Throwable throwable) {
        this.enable();

        SentryEvent event = new SentryEvent();

        event.setThrowable(throwable);
        event.setTag("Type", "Submitted");

        this.addExtraInfo(event);
        Sentry.captureEvent(event);

        this.disable();
    }

    private void addExtraInfo(SentryEvent event) {
        event.setLevel(SentryLevel.ERROR);
        event.setServerName("");

        User user = new User();

        try(Config config = ConfigManager.get().getConfig(true)) {
            user.setId(config.user.uuid);
        }

        event.setUser(user);
        event.setRelease(Const.get().version);

        String ideName = ApplicationInfo.getInstance().getFullApplicationName();
        event.setTag("IDE", ideName);
    }
}
