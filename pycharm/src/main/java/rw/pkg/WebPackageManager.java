// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package rw.pkg;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
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

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.lingala.zip4j.ZipFile;
import rw.pkg.wheel.BaseWheel;
import rw.pkg.wheel.WheelFactory;
import rw.util.OsType;


public final class WebPackageManager extends BasePackageManager {
    public WebPackageManager() {
        super();
    }

    public boolean isInstalled() {
        String fromWeb = this.getLatestVersionFromWeb();
        String local = this.getCurrentVersion();

        if (local == null || fromWeb == null) {
            return false;
        }

        boolean ret = fromWeb.equals(local);
        return ret;
    }

    @Override
    public void install(@Nullable Listener listener) throws Exception {
        String version = this.getLatestVersionFromWeb();
        if (listener != null)
            listener.started();
        for (BaseWheel wheel : this.getWheelUrlsForVersion(version)) {
            Path packageVersionDir = wheel.getPackageDir();

            if (Files.exists(packageVersionDir))
                FileUtils.deleteDirectory(packageVersionDir.toFile());

            File wheelFile = new File(packageVersionDir.toString(), wheel.getFilename());

            URL url = new URL(wheel.getInput());
            URLConnection urlConnection = url.openConnection();
            if (Config.get().stage != Stage.PROD) {
                String basicAuthenticationEncoded = Base64.getEncoder().encodeToString(
                        String.format("%s:%s", Config.get().pypiUsername, Config.get().pypiPassword).getBytes(StandardCharsets.UTF_8)
                );
                urlConnection.setRequestProperty("Authorization", "Basic " + basicAuthenticationEncoded);
            }

            File parent = new File(wheelFile.getParent());
            parent.mkdirs();

            FileOutputStream wheelFileStream = new FileOutputStream(wheelFile);

            IOUtils.copy(urlConnection.getInputStream(), wheelFileStream);
            wheelFileStream.close();
            new ZipFile(wheelFile).extractAll(parent.toString());
            wheelFile.delete();
        }
        Files.writeString(this.currentVersionFile, version);
    }

    public String getLatestVersionFromWeb() {
        List<BaseWheel> allWheels = this.getAllWheelUrls();
        if (allWheels.isEmpty()) {
            return null;
        }
        BaseWheel latestWheelUrl = allWheels.get(0);

        return latestWheelUrl.getVersion();
    }

    public List<BaseWheel> getAllWheelUrls() {
        List<BaseWheel> ret = new ArrayList<>();

        String requestUrl = Config.get().pypiUrl + "/simple/" + Config.get().packageName;

        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        int timeout = 10;
        requestBuilder.setConnectTimeout(timeout * 1000);
        requestBuilder.setConnectionRequestTimeout(timeout * 1000);
        requestBuilder.setSocketTimeout(timeout * 1000);

        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestBuilder.build()).build();
        HttpGet httpGet = new HttpGet(requestUrl);

        if (!Config.get().pypiUsername.equals("None")) {
            httpGet.addHeader("Authorization", BasicScheme.authenticate(
                    new UsernamePasswordCredentials(Config.get().pypiUsername, Config.get().pypiPassword), "UTF-8"));
        }

        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            String responseString = new BasicResponseHandler().handleResponse(httpResponse);
            Matcher m = Pattern.compile("<a href=\"(.*?)\".*</a><br/?>").matcher(responseString);
            while (m.find()) {
                String url = m.group(1);

                if (Arrays.asList(Stage.LOCAL, Stage.CI, Stage.STAGE).contains(Config.get().stage)) {
                    url = String.format("%s%s", Config.get().pypiUrl, url);
                }

                BaseWheel wheelUrl = WheelFactory.factory(url);

                if (wheelUrl.getOsType() != OsType.DETECTED) {
                    continue;
                }

                ret.add(wheelUrl);
            }
            httpClient.close();
        } catch (IOException e) {
            RwSentry.get().captureException(e);
        }
        Collections.reverse(ret);
        return ret;
    }

    public List<BaseWheel> getWheelUrlsForVersion(String version) {
        List<BaseWheel> ret = new ArrayList<>();

        for (BaseWheel wheelUrl : this.getAllWheelUrls()) {
            if (wheelUrl.getVersion().equals(version)) {
                ret.add(wheelUrl);
            }
        }
        return ret;
    }
}
