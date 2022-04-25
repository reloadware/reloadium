package rw.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.VisibleForTesting;
import rw.audit.RwSentry;
import rw.consts.Const;

import java.io.IOException;
import java.util.UUID;


public class ConfigManager {
    @VisibleForTesting
    public static ConfigManager singleton = null;

    @VisibleForTesting
    public ConfigManager() {
    }

    public static ConfigManager get() {
        if (singleton == null) {
            singleton = new ConfigManager();
        }
        return singleton;
    }

    public void createIfNotExists() {
        if (Const.get().getConfigFile().exists()) {
            return;
        }

        Config config = new Config();
        config.user.uuid = UUID.randomUUID().toString();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            FileUtils.writeStringToFile(Const.get().getConfigFile(), gson.toJson(config), "utf-8");
        } catch (IOException e) {
            RwSentry.get().captureException(e);
        }
    }

    public Config getConfig() {
        ConfigManager.get().createIfNotExists();

        Gson g = new Gson();
        Config ret = null;
        try {
            ret = g.fromJson(FileUtils.readFileToString(Const.get().getConfigFile(), "utf-8"), Config.class);
        } catch (IOException e) {
            RwSentry.get().captureException(e);
        }
        return ret;
    }
}
