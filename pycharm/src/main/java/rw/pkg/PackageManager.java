// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package rw.pkg;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOExceptionList;
import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import rw.audit.RwSentry;
import rw.consts.Const;
import rw.pkg.wheel.BaseWheel;
import rw.pkg.wheel.WheelFactory;
import rw.util.Architecture;
import rw.pkg.InstallTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
    String resourceWheelsPathRoot = "META-INF/wheels/";

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
                    this.resourceWheelsPathRoot + "version.txt"), StandardCharsets.UTF_8.name());
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

    public void run(@Nullable Listener listener) {
        try {
            this.installing = true;
            LOGGER.info("Installing");
            ProgressManager.getInstance().run(new InstallTask(this, listener));
        } catch (Exception e) {
            RwSentry.get().captureException(e, true);
        }
    }

    protected void installWheels(List<File> wheels) throws Exception {
        for (File wheelFile : wheels) {
            BaseWheel wheel = WheelFactory.factory(wheelFile.getName());

            File packageVersionDir = this.fs.getPackagePythonVersionDir(wheel.getPythonVersion());

            if (packageVersionDir.exists()) {
                try {
                    FileUtils.deleteDirectory(packageVersionDir);
                } catch (IOException ignored) {
                }
            }
            packageVersionDir.mkdirs();

            String tmpdir = Files.createTempDirectory(Const.get().packageName).toFile().getAbsolutePath();
            new ZipFile(wheelFile).extractAll(tmpdir);

            this.fs.putDirectory(new File(tmpdir), packageVersionDir);

            try {
                wheelFile.delete();
            } catch (Exception ignored) {
            }
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

    protected void installVersion(@Nullable Listener listener, String version) throws Exception {
        if (listener != null)
            listener.started();

        List<File> wheels = this.getWheelFiles();
        if (wheels.isEmpty()) {
            return;
        }
        this.installWheels(wheels);
        this.installLauncher();
        this.fs.writeString(this.currentVersionFile, version);
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

    public String getBuiltinVersion() {
        return this.builtinVersion;
    }

    public void install(@Nullable Listener listener) throws Exception {
        LOGGER.info("Installing");
        this.installVersion(listener, this.builtinVersion);
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

    public List<File> getWheelFiles() throws IOException {
        LOGGER.info("Getting wheels files");

        List<File> ret = new ArrayList<>();

        String tmpdir = Files.createTempDirectory(Const.get().packageName).toFile().getAbsolutePath();

        String[] wheels = null;
        wheels = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(
                this.resourceWheelsPathRoot + "content.txt")), StandardCharsets.UTF_8.name()).split("\n");

        for (String wheelFilename : wheels) {
            LOGGER.info("Getting " + wheelFilename);

            BaseWheel wheel = WheelFactory.factory(wheelFilename);

            if (wheel.getOsType() != this.machine.getOsType() || wheel.getArchitecture() != this.machine.getArchitecture()) {
                continue;
            }

            File wheelFile = new File(tmpdir, wheel.getFilename());

            InputStream wheelFileStream = getClass().getClassLoader().getResourceAsStream(
                    this.resourceWheelsPathRoot + wheelFilename
            );

            if (wheelFileStream == null) {
                continue;
            }

            FileUtils.copyInputStreamToFile(wheelFileStream, wheelFile);
            wheelFileStream.close();
            ret.add(wheelFile);
        }
        return ret;
    }

    public FileSystem getFs() {
        return this.fs;
    }
}
