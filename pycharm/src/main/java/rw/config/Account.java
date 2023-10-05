package rw.config;

import com.google.gson.annotations.SerializedName;

public class Account {
    @SerializedName("license_key")
    public String licenseKey;

    @SerializedName("last_license_type")
    public String lastLicenseType;
}
