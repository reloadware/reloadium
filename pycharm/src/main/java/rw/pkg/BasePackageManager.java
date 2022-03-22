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
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    abstract boolean isInstalled();

    public boolean isInstalling() {
        return this.installing;
    }

    abstract public void install(@Nullable Listener listener) throws Exception ;
    public void run(@Nullable Listener listener) {
        if (!this.isInstalled()) {
            this.installing = true;
            ProgressManager.getInstance().run(new InstallTask(this, listener));
        }
    }
}
