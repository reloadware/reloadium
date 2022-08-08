// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package rw.pkg;

import com.intellij.openapi.diagnostic.Logger;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.BasicScheme;
import org.apache.commons.io.IOExceptionList;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.Nullable;
import rw.audit.RwSentry;
import rw.consts.Const;
import rw.consts.Stage;
import rw.pkg.wheel.BaseWheel;
import rw.pkg.wheel.WheelFactory;
import rw.util.OsType;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class WebPackageManager extends BasePackageManager {
    @Nullable
    List<BaseWheel> wheelUrlsCache;
    long wheelUrlsCacheCheckTimestamp;

    private static final Logger LOGGER = Logger.getInstance(WebPackageManager.class);

    public WebPackageManager() {
        super();

        LOGGER.info("Creating WebPackageManager");

        this.wheelUrlsCacheCheckTimestamp = Instant.now().getEpochSecond();
        this.wheelUrlsCache = null;
    }

    public boolean shouldInstall() {
        String fromWeb = this.getLatestVersionFromWeb();
        if (fromWeb == null) {
            return false;
        }

        String local = this.getCurrentVersion();

        if (local == null) {
            return true;
        }

        boolean ret = !fromWeb.equals(local);
        return ret;
    }

    @Override
    public void install(@Nullable Listener listener) throws Exception {
        LOGGER.info("Installing");

        String version = this.getLatestVersionFromWeb();
        this.installVersion(listener, version);
    }

    protected List<File> getWheelFiles() throws IOException, IOExceptionList {
        LOGGER.info("Getting wheels files");

        String version = this.getLatestVersionFromWeb();
        String tmpdir = Files.createTempDirectory(Const.get().packageName).toFile().getAbsolutePath();
        List<File> ret = new ArrayList<>();

        for (BaseWheel wheel : this.getWheelUrlsForVersion(version)) {
            wheel.initPackageDir();

            File wheelFile = new File(tmpdir, wheel.getFilename());

            URL url = new URL(wheel.getInput());
            URLConnection urlConnection = url.openConnection();
            if (Const.get().stage != Stage.PROD) {
                String basicAuthenticationEncoded = Base64.getEncoder().encodeToString(
                        String.format("%s:%s", Const.get().pypiUsername, Const.get().pypiPassword).getBytes(StandardCharsets.UTF_8)
                );
                urlConnection.setRequestProperty("Authorization", "Basic " + basicAuthenticationEncoded);
            }

            FileOutputStream wheelFileStream = new FileOutputStream(wheelFile);
            IOUtils.copy(urlConnection.getInputStream(), wheelFileStream);
            wheelFileStream.close();
            ret.add(wheelFile);
        }
        return ret;
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
        LOGGER.info("Getting all wheels urls");
        if (Instant.now().getEpochSecond() - wheelUrlsCacheCheckTimestamp < 60.0 && this.wheelUrlsCache != null) {
            LOGGER.info("Using cached.");
            return this.wheelUrlsCache;
        }
        LOGGER.info("Cache obsolete, fetching from web");

        this.wheelUrlsCacheCheckTimestamp = Instant.now().getEpochSecond();

        List<BaseWheel> ret = new ArrayList<>();

        String requestUrl = Const.get().pypiUrl + "/simple/" + Const.get().packageName;

        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        int timeout = 10;
        requestBuilder.setConnectTimeout(timeout * 1000);
        requestBuilder.setConnectionRequestTimeout(timeout * 1000);
        requestBuilder.setSocketTimeout(timeout * 1000);

        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestBuilder.build()).build();
        HttpGet httpGet = new HttpGet(requestUrl);

        if (!Const.get().pypiUsername.equals("None")) {
            httpGet.addHeader("Authorization", BasicScheme.authenticate(
                    new UsernamePasswordCredentials(Const.get().pypiUsername, Const.get().pypiPassword), "UTF-8"));
        }

        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            String responseString = new BasicResponseHandler().handleResponse(httpResponse);
            Matcher m = Pattern.compile("<a href=\"(.*?)\".*</a><br/?>").matcher(responseString);
            while (m.find()) {
                String url = m.group(1);

                if (Arrays.asList(Stage.LOCAL, Stage.CI, Stage.STAGE).contains(Const.get().stage)) {
                    url = String.format("%s%s", Const.get().pypiUrl, url);
                }

                BaseWheel wheelUrl = WheelFactory.factory(url);

                if (wheelUrl.getOsType() != OsType.DETECTED) {
                    continue;
                }

                ret.add(wheelUrl);
            }
            httpClient.close();
        } catch (SocketException | SocketTimeoutException | ConnectTimeoutException | UnknownHostException | SSLException ignored) {
        } catch (IOException e) {
            RwSentry.get().captureException(e);
        }
        Collections.reverse(ret);

        this.wheelUrlsCache = ret;
        return ret;
    }

    public List<BaseWheel> getWheelUrlsForVersion(String version) {
        LOGGER.info("Getting wheel urls for version " + version);
        List<BaseWheel> ret = new ArrayList<>();

        for (BaseWheel wheelUrl : this.getAllWheelUrls()) {
            if (wheelUrl.getVersion().equals(version)) {
                ret.add(wheelUrl);
            }
        }
        return ret;
    }
}
