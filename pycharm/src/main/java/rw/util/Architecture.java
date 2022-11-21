package rw.util;

import com.intellij.jna.JnaLoader;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.ptr.IntByReference;
import rw.audit.RwSentry;

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
            String arch = System.getProperty("os.arch");
            if (isUnderRosetta() || arch.contains("arm")) {
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
    static private boolean isUnderRosetta() {
        try {
            if (JnaLoader.isLoaded()) {
                IntByReference p = new IntByReference();
                SystemB.size_t.ByReference size = new SystemB.size_t.ByReference(SystemB.INT_SIZE);
                if (SystemB.INSTANCE.sysctlbyname("sysctl.proc_translated", p.getPointer(), size, null, SystemB.size_t.ZERO) != -1) {
                    return p.getValue() == 1;
                }
            }
        }
        catch (Throwable t) {
            RwSentry.get().captureException(t);
        }
        return false;
    }
}
