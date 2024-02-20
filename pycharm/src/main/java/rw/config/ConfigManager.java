package rw.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.VisibleForTesting;
import rw.audit.RwSentry;
import rw.pkg.NativeFileSystem;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ConfigManager {
    @VisibleForTesting
    public static ConfigManager singleton = null;

    @VisibleForTesting
    public NativeFileSystem fs;

    Lock lock;

    @VisibleForTesting
    public ConfigManager() {
        this.fs = NativeFileSystem.get();
        this.lock = new ReentrantLock();
    }

    public static ConfigManager get() {
        if (singleton == null) {
            singleton = new ConfigManager();
        }
        return singleton;
    }

    public void createIfNotExists() {
        if (this.fs.getConfigFile().exists()) {
            return;
        }

        this.lock();

        Config config = new Config(false);
        config.init();

        this.save(config);
        this.unlock();
    }

    void save(Config config) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            FileUtils.writeStringToFile(this.fs.getConfigFile(), gson.toJson(config), "utf-8");
        } catch (IOException e) {
            RwSentry.get().captureException(e, false);
        }
    }

    public Config getConfig(boolean readOnly) {
        this.createIfNotExists();

        this.lock();

        Gson g = new Gson();
        Config ret = null;
        try {
            ret = g.fromJson(FileUtils.readFileToString(this.fs.getConfigFile(), "utf-8"), Config.class);
            ret.readOnly = readOnly;
        } catch (IOException e) {
            RwSentry.get().captureException(e, false);
        }

        return ret;
    }

    public void lock() {
        this.lock.lock();
    }

    public void unlock() {
        this.lock.unlock();
    }
}
