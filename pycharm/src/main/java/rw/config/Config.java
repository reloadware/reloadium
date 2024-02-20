package rw.config;

import rw.haven.license.UnknownLicense;

import java.util.UUID;

public class Config implements AutoCloseable {
    public User user;
    public Account account;

    transient boolean readOnly;

    Config(boolean readOnly) {
        this.readOnly = readOnly;
        this.init();
    }

    public void init(){
        this.user = new User();
        this.account = new Account();

        this.user.uuid = UUID.randomUUID().toString();

        this.account.lastLicenseType = UnknownLicense.TYPE;
        this.account.licenseKey = "";
    }

    @Override
    public void close() {
        if (!this.readOnly) {
            ConfigManager.get().save(this);
        }

        ConfigManager.get().unlock();
    }
}
