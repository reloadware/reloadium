package rw.pkg;

import org.apache.commons.io.IOUtils;
import rw.util.OsType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public enum Architecture {
    X64("x64"),
    ARM64("arm64");

    public final String value;

    public static final Architecture DETECTED;

    Architecture(String value) {
        this.value = value;
    }

    static {
        if (OsType.DETECTED == OsType.MacOS) {
            File rosetta = new File("/usr/libexec/rosetta/");
            String arch = System.getProperty("os.arch");
            if (rosetta.exists() || arch.contains("arm")) {
                DETECTED = ARM64;
            }
            else {
                DETECTED = X64;
            }
        }
        else {
            DETECTED = X64;
        }
    }
}
