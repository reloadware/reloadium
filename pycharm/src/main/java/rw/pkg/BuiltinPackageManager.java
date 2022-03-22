// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package rw.pkg;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.BasicScheme;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.audit.RwSentry;
import rw.config.Config;
import rw.config.Stage;
import rw.pkg.wheel.BaseWheel;
import rw.pkg.wheel.WheelFactory;
import rw.util.OsType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public final class BuiltinPackageManager extends BasePackageManager {
    boolean installing;
    File builtinWheelsDir;
    String builtinVersion;

    public BuiltinPackageManager() {
        super();
        this.builtinWheelsDir = new File(this.getClass().getClassLoader().getResource("wheels").getFile());
        try {
            String builtinVersionStr = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("wheels/version.txt"), StandardCharsets.UTF_8.name());
            this.builtinVersion = builtinVersionStr;
        } catch (IOException e) {
            RwSentry.get().captureException(e);
        }
    }

    @Nullable
    public String getBuiltinVersion() {
        return this.builtinVersion;
    }


    public boolean isInstalled() {
        boolean ret;

        if (this.getCurrentVersion() == null) {
            ret = false;
        }
        else {
            ComparableVersion builtinVersion = new ComparableVersion(this.getBuiltinVersion());
            ComparableVersion currentVersion = new ComparableVersion(this.getCurrentVersion());

            ret = builtinVersion.compareTo(currentVersion) > 0;
        }
        return ret;
    }

    public boolean isInstalling() {
        return this.installing;
    }

    public void install(@Nullable Listener listener) throws Exception {
        if (listener != null)
            listener.started();

        String[] wheels = null;
        try {
            wheels = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(
                    "wheels/content.txt")), StandardCharsets.UTF_8.name()).split("\n");
        } catch (IOException e) {
            RwSentry.get().captureException(e);
            throw e;
        }

        for (String wheelFilename : wheels) {
            BaseWheel wheel = WheelFactory.factory(wheelFilename);

            if (wheel.getOsType() != OsType.DETECTED) {
                continue;
            }

            Path packageVersionDir = wheel.getPackageDir();

            if (Files.exists(packageVersionDir))
                FileUtils.deleteDirectory(packageVersionDir.toFile());

            File wheelFile = new File(packageVersionDir.toString(), wheel.getFilename());

            File parent = new File(wheelFile.getParent());
            parent.mkdirs();

            InputStream wheelFileStream = getClass().getClassLoader().getResourceAsStream(String.format("wheels/%s", wheelFilename));
            FileUtils.copyInputStreamToFile(wheelFileStream, wheelFile);

            wheelFileStream.close();
            new ZipFile(wheelFile).extractAll(parent.toString());
            wheelFile.delete();
        }
        Files.writeString(this.currentVersionFile, this.builtinVersion.toString());
    }
}
