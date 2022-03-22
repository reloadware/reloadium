package rw.util;

import java.util.Locale;

public enum OsType {
    Windows, MacOS, Linux, Other;
    public static final OsType DETECTED;

    static {
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((OS.contains("mac")) || (OS.contains("darwin"))) {
            DETECTED = OsType.MacOS;
        } else if (OS.contains("win")) {
            DETECTED = OsType.Windows;
        } else if (OS.contains("nux")) {
            DETECTED = OsType.Linux;
        } else {
            DETECTED = OsType.Other;
        }
    }
}
