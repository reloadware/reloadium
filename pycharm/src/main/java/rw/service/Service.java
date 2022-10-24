package rw.service;

import com.intellij.concurrency.JobScheduler;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.kohsuke.rngom.parse.host.Base;
import rw.audit.RwSentry;
import rw.consts.Const;
import rw.handler.runConf.BaseRunConfHandler;
import rw.pkg.BuiltinPackageManager;
import rw.util.OsType;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class Service implements Disposable {
    private static final Logger LOGGER = Logger.getInstance(Service.class);
    @VisibleForTesting
    public BuiltinPackageManager builtinPackageManager;

    public static Service singleton = null;

    @VisibleForTesting
    public Service() {
        LOGGER.info("Starting service");
        this.builtinPackageManager = new BuiltinPackageManager();
        this.validateOsType();
        this.init();
    }

    public void init() {
        LOGGER.info("Initializing service");
        this.builtinPackageManager.run(null);

        JobScheduler.getScheduler().scheduleWithFixedDelay(this::checkIfStillGood, 2,
                10, TimeUnit.MINUTES);
    }

    public static Service get() {
        if (singleton == null) {
            singleton = ApplicationManager.getApplication().getService(Service.class);
        }

        return singleton;
    }

    public boolean canRun(RunnerAndConfigurationSettings settings) {
        return this.builtinPackageManager.getCurrentVersion() != null;
    }

    private void validateOsType() {
        if (OsType.DETECTED == OsType.Other) {
            String osName = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            RwSentry.get().captureError(String.format("Unsupported os type %s", osName));
        }
    }

    public void checkIfStillGood() {
        LOGGER.info("Checking if still good");
        if (this.builtinPackageManager.shouldInstall() && !this.builtinPackageManager.isInstalling()) {
            LOGGER.info("Not good, installing builtin package");
            this.builtinPackageManager.run(null);
        }
    }

    @Override
    public void dispose() {

    }
}