package rw.audit;

import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.protocol.Message;
import org.jetbrains.annotations.VisibleForTesting;
import rw.config.Config;


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
            options.setDsn(Config.get().sentryDsn);
            options.setEnvironment(Config.get().stage.value);
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

    public void captureException(Throwable throwable) {
        this.enable();

        SentryEvent event = new SentryEvent();

        event.setThrowable(throwable);
        this.addExtraInfo(event);
        Sentry.captureEvent(event);

        this.disable();
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
        event.setRelease(Config.get().version);
    }
}
