// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package rw.pkg;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import rw.audit.RwSentry;
import rw.consts.Const;
import rw.pkg.wheel.BaseWheel;
import rw.pkg.wheel.WheelFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class PackageManager {
    private static final Logger LOGGER = Logger.getInstance(PackageManager.class);

    @NotNull
    protected final File currentVersionFile;
    protected final File launcherFile;
    boolean installing;

    String builtinVersion;

    String pythonFilesRoot = "META-INF/python_files/";

    @VisibleForTesting
    public FileSystem fs;
    Machine machine;

    public static class Listener {
        public void started() {
        }

        public void success() {
        }

        public void fail(Exception exception) {
        }

        public void cancelled() {
        }
    }

    public PackageManager() {
        this(NativeFileSystem.get(), new NativeMachine());
    }

    public PackageManager(FileSystem fs, Machine machine) {
        LOGGER.info(String.format("Creating PackageManager (%s)", fs.getClass().getName()));

        this.fs = fs;
        this.machine = machine;

        try {
            String builtinVersionStr = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(
                    BaseWheel.RESOURCE_WHEELS_PATH_ROOT + "version.txt"), StandardCharsets.UTF_8.name());
            this.builtinVersion = builtinVersionStr;
        } catch (IOException e) {
            RwSentry.get().captureException(e, true);
        }

        this.currentVersionFile = new File(this.fs.getPackagesRootDir(), "version.txt");
        this.launcherFile = new File(this.fs.getPackagesRootDir(), Const.get().launcherName + ".py");
        this.installing = false;
    }

    @Nullable
    public String getCurrentVersion() {
        try {
            return this.fs.readString(this.currentVersionFile).strip();
        } catch (IOException e) {
            return null;
        }
    }

    public boolean isInstalling() {
        return this.installing;
    }

    public void run(@Nullable Listener listener, boolean fail) {
        try {
            this.installing = true;
            LOGGER.info("Installing");
            ProgressManager.getInstance().run(new InstallTask(this, listener, fail));
        } catch (Exception e) {
            RwSentry.get().captureException(e, true);
        }
    }

    protected void installWheels(List<BaseWheel> wheels) throws Exception {
        for (BaseWheel wheel : wheels) {
            wheel.unpack(this.fs);
        }
    }

    protected void installLauncher() {
        try {
            String Content = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(
                    this.pythonFilesRoot + Const.get().launcherName + ".py"), StandardCharsets.UTF_8.name());
            this.fs.writeString(this.launcherFile, Content);
        } catch (IOException e) {
            RwSentry.get().captureException(e, true);
        }
    }

    protected void installVersion(String version) throws Exception {
        List<BaseWheel> wheels = this.getWheels();
        if (wheels.isEmpty()) {
            return;
        }
        this.installWheels(wheels);
        this.installLauncher();
        this.fs.writeString(this.currentVersionFile, version);
    }

    public String getBuiltinVersion() {
        return this.builtinVersion;
    }

    public void install() throws Exception {
        LOGGER.info("Installing");
        this.installVersion(this.builtinVersion);
    }

    public boolean shouldInstall() {
        if (this.getCurrentVersion() == null) {
            return true;
        }

        ComparableVersion builtinVersion = new ComparableVersion(this.getBuiltinVersion());
        ComparableVersion currentVersion = new ComparableVersion(this.getCurrentVersion());

        boolean ret = builtinVersion.compareTo(currentVersion) != 0;
        return ret;
    }

    public List<BaseWheel> getWheels() throws IOException {
        LOGGER.info("Getting wheels files");

        List<BaseWheel> ret = new ArrayList<>();

        String[] wheelFilenames = null;
        wheelFilenames = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(
                BaseWheel.RESOURCE_WHEELS_PATH_ROOT + "content.txt")), StandardCharsets.UTF_8.name()).split("\n");

        for (String wheelFilename : wheelFilenames) {
            LOGGER.info("Getting " + wheelFilename);

            BaseWheel wheel = WheelFactory.factory(wheelFilename);

            if (!wheel.accepts(this.machine)) {
                continue;
            }

            ret.add(wheel);
        }
        return ret;
    }

    public FileSystem getFs() {
        return this.fs;
    }
}
