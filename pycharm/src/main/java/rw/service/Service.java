package rw.service;

import com.intellij.concurrency.JobScheduler;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.log4j.Level;
import org.jetbrains.annotations.VisibleForTesting;
import rw.audit.RwSentry;
import rw.config.Config;
import rw.pkg.BuiltinPackageManager;
import rw.pkg.WebPackageManager;
import rw.util.OsType;

import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class Service {
    private static final Logger LOGGER = Logger.getInstance(Service.class);
    @VisibleForTesting
    public BuiltinPackageManager builtinPackageManager;
    @VisibleForTesting
    public WebPackageManager webPackageManager;

    public static Service singleton = null;

    @VisibleForTesting
    public Service() {
        LOGGER.info("Starting service");
        this.webPackageManager = new WebPackageManager();
        this.builtinPackageManager = new BuiltinPackageManager();
        this.validateOsType();
        this.init();
    }

    public void init() {
        LOGGER.info("Initializing service");
        this.builtinPackageManager.run(null);

        JobScheduler.getScheduler().scheduleWithFixedDelay(this::checkForUpdate, 1,
                Config.get().checkForUpdateInterval, TimeUnit.HOURS);

        JobScheduler.getScheduler().scheduleWithFixedDelay(this::checkIfStillGood, 120,
                30, TimeUnit.SECONDS);
    }

    public static Service get() {
        if (singleton == null) {
            singleton = ApplicationManager.getApplication().getService(Service.class);
        }

        return singleton;
    }

    public WebPackageManager getPackageManager(){
        return this.webPackageManager;
    }

    public boolean canRun(RunnerAndConfigurationSettings settings) {
        return this.webPackageManager.getCurrentVersion() != null;
    }

    private void validateOsType() {
        if (OsType.DETECTED == OsType.Other) {
            String osName = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            RwSentry.get().captureError(String.format("Unsupported os type %s", osName));
        }
    }

    public void checkForUpdate() {
        if (this.webPackageManager.hasErrored()) {
            return;
        }

        this.webPackageManager.run(null);
    }

    public void checkIfStillGood() {
        if (this.webPackageManager.hasErrored()) {
            return;
        }

        if (this.webPackageManager.shouldInstall() && !this.webPackageManager.isInstalling() && !this.builtinPackageManager.isInstalling()) {
            this.webPackageManager.run(null);
        }
    }
}