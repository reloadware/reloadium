// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package rw.pkg;

import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOExceptionList;
import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.jetbrains.annotations.Nullable;
import rw.audit.RwSentry;
import rw.consts.Const;
import rw.pkg.wheel.BaseWheel;
import rw.pkg.wheel.WheelFactory;
import rw.util.OsType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public final class BuiltinPackageManager extends BasePackageManager {
    String builtinVersion;
    String resourceWheelsPathRoot = "META-INF/wheels/";

    private static final Logger LOGGER = Logger.getInstance(BuiltinPackageManager.class);

    public BuiltinPackageManager() {
        super();
        LOGGER.info("Creating BuiltinPackageManager");

        try {
            String builtinVersionStr = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(
                    this.resourceWheelsPathRoot + "version.txt"), StandardCharsets.UTF_8.name());
            this.builtinVersion = builtinVersionStr;
        } catch (IOException e) {
            RwSentry.get().captureException(e);
        }
    }

    @Nullable
    public String getBuiltinVersion() {
        return this.builtinVersion;
    }

    @Override
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

    public List<File> getWheelFiles() throws IOException, IOExceptionList {
        LOGGER.info("Getting wheels files");

        List<File> ret = new ArrayList<>();

        String tmpdir = Files.createTempDirectory(Const.get().packageName).toFile().getAbsolutePath();

        String[] wheels = null;
        try {
            wheels = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(
                    this.resourceWheelsPathRoot + "content.txt")), StandardCharsets.UTF_8.name()).split("\n");
        } catch (IOException e) {
            RwSentry.get().captureException(e);
            throw e;
        }

        for (String wheelFilename : wheels) {
            LOGGER.info("Getting " + wheelFilename);

            BaseWheel wheel = WheelFactory.factory(wheelFilename);

            if (wheel.getOsType() != OsType.DETECTED || wheel.getArchitecture() != Architecture.DETECTED) {
                continue;
            }

            File wheelFile = new File(tmpdir, wheel.getFilename());

            InputStream wheelFileStream = getClass().getClassLoader().getResourceAsStream(
                    this.resourceWheelsPathRoot + wheelFilename
            );
            FileUtils.copyInputStreamToFile(wheelFileStream, wheelFile);
            wheelFileStream.close();
            ret.add(wheelFile);
        }
        return ret;
    }
}
