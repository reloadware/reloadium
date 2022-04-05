// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package rw.pkg;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import net.lingala.zip4j.ZipFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.audit.RwSentry;
import rw.config.Config;
import rw.pkg.wheel.BaseWheel;
import rw.pkg.wheel.WheelFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class InstallTask extends Task.Backgroundable {
    @Nullable
    protected BasePackageManager packageManager;

    @Nullable
    BasePackageManager.Listener listener;

    InstallTask(@NotNull BasePackageManager packageManager,
                @Nullable BasePackageManager.Listener listener) {
        super(null, Config.get().msgs.INSTALLING_PACKAGE);
        this.packageManager = packageManager;
        this.listener = listener;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        indicator.setText(this.getTaskText());
        runTask(indicator);
    }

    @NotNull
    protected String getTaskText() {
        return Config.get().msgs.INSTALLING_PACKAGE;
    }

    protected void runTask(@NotNull ProgressIndicator indicator) {
        try {
            this.packageManager.install(null);
            if (this.listener != null)
                this.listener.success();
        } catch (Exception e) {
            RwSentry.get().captureException(e);
            this.packageManager.setErrored();
            if (this.listener != null)
                this.listener.fail(e);
        } finally {
            this.packageManager.installing = false;
        }
    }
}


public abstract class BasePackageManager {
    private static final Logger LOGGER = Logger.getInstance(BasePackageManager.class);

    @NotNull
    protected final Path currentVersionFile;
    boolean installing;
    private boolean errored;

    public static class Listener {
        public void started() {

        }

        public void success() {

        }

        public void fail(Exception exception) {

        }
    }

    public BasePackageManager() {
        this.errored = false;
        this.currentVersionFile = Paths.get(String.valueOf(Config.get().getPackagesRootDir()), "version.txt");
        this.installing = false;
    }

    void setErrored() {
        this.errored = true;
    }

    public boolean hasErrored() {
        return this.errored;
    }

    @Nullable
    public String getCurrentVersion() {
        try {
            return Files.readString(this.currentVersionFile);
        } catch (IOException e) {
            return null;
        }
    }

    abstract boolean shouldInstall();

    public boolean isInstalling() {
        return this.installing;
    }

    abstract public void install(@Nullable Listener listener) throws Exception;

    public void run(@Nullable Listener listener) {
        if (this.shouldInstall()) {
            this.installing = true;
            LOGGER.info("Installing");
            ProgressManager.getInstance().run(new InstallTask(this, listener));
        }
    }

    abstract protected List<File> getWheelFiles() throws IOException;

    protected void installWheels(List<File> wheels) throws Exception {
        for (File wheelFile : wheels) {
            BaseWheel wheel = WheelFactory.factory(wheelFile.getName());

            File packageVersionDir = wheel.getPackageDir();
            wheel.initPackageDir();

            new ZipFile(wheelFile).extractAll(packageVersionDir.toString());
            try {
                wheelFile.delete();
            } catch (Exception ignored) {
            }
        }
    }

    protected void installVersion(@Nullable Listener listener, String version) throws Exception {
        if (listener != null)
            listener.started();

        List<File> wheels = this.getWheelFiles();
        this.installWheels(wheels);
        Files.writeString(this.currentVersionFile, version);
        this.cleanWheels(wheels);
    }
    protected void cleanWheels(List<File> wheels) {
        for (File w : wheels) {
            try {
                w.delete();
            } catch (Exception ignored) {
            }
        }
    }
}
