package rw.util;
import java.io.File;

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

            boolean m1Override = System.getenv("RW_M1") != null;

            if (rosetta.exists() || arch.contains("arm") || m1Override) {
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
