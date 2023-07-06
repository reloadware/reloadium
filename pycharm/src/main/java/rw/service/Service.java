package rw.service;

import com.intellij.concurrency.JobScheduler;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.VisibleForTesting;
import rw.ai.context.ContextManager;
import rw.audit.RwSentry;
import rw.pkg.PackageManager;
import rw.remote.RemoteSdkChecker;
import rw.util.OsType;

import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class Service implements Disposable {
    private static final Logger LOGGER = Logger.getInstance(Service.class);
    public static Service singleton = null;
    private static int runCounter = 0;
    private final RemoteSdkChecker remoteSdkChecker;
    @VisibleForTesting
    public PackageManager packageManager;

    public Service() {
        LOGGER.info("Starting service");
        this.packageManager = new PackageManager();
        this.remoteSdkChecker = new RemoteSdkChecker();
        this.validateOsType();
        this.init();
    }

    public static Service get() {
        if (singleton == null) {
            singleton = ApplicationManager.getApplication().getService(Service.class);
        }
        return singleton;
    }

    public void init() {
        LOGGER.info("Initializing service");
        this.packageManager.run(null, true);
        ContextManager.get().addListeners(this);

        JobScheduler.getScheduler().scheduleWithFixedDelay(this::checkIfStillGood, 2,
                10, TimeUnit.MINUTES);

        JobScheduler.getScheduler().scheduleWithFixedDelay(this.remoteSdkChecker::check, 30,
                60, TimeUnit.SECONDS);
    }

    private void validateOsType() {
        if (OsType.DETECTED == OsType.Other) {
            String osName = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            RwSentry.get().captureError(String.format("Unsupported os type %s", osName));
        }
    }

    public void checkIfStillGood() {
        LOGGER.info("Checking if still good");
        if (this.packageManager.shouldInstall() && !this.packageManager.isInstalling()) {
            LOGGER.info("Not good, installing builtin package");
            this.packageManager.run(null, true);
        }
    }

    public PackageManager getPackageManager() {
        return this.packageManager;
    }

    public RemoteSdkChecker getRemoteSdkChecker() {
        return this.remoteSdkChecker;
    }

    @Override
    public void dispose() {
    }

    public void onRun() {
        runCounter += 1;
    }

    public int getRunCounter() {
        return runCounter;
    }
}