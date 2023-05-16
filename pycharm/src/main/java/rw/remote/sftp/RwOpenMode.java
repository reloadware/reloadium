package rw.remote.sftp;

import rw.audit.RwSentry;

import java.lang.reflect.Field;
import java.util.Set;

public enum RwOpenMode {

    /**
     * Open the file for reading.
     */
    READ(0x00000001),
    /**
     * Open the file for writing. If both this and {@link RwOpenMode#READ} are specified, the file is opened for both
     * reading and writing.
     */
    WRITE(0x00000002),
    /**
     * Force all writes to append data at the end of the file.
     */
    APPEND(0x00000004),
    /**
     * If this flag is specified, then a new file will be created if one does not already exist (if {@link
     * RwOpenMode#TRUNC} is specified, the new file will be truncated to zero length if it previously exists).
     */
    CREAT(0x00000008),
    /**
     * Forces an existing file with the same name to be truncated to zero length when creating a file by specifying
     * {@link RwOpenMode#CREAT}. {@link RwOpenMode#CREAT} MUST also be specified if this flag is used.
     */
    TRUNC(0x00000010),
    /**
     * Causes the request to fail if the named file already exists. {@link RwOpenMode#CREAT} MUST also be specified if
     * this flag is used.
     */
    EXCL(0x00000020);

    public static Class NativeClass = getNativeClass();
    private final int pflag;

    private RwOpenMode(int pflag) {
        this.pflag = pflag;
    }

    public static int toMask(Set<RwOpenMode> modes) {
        int mask = 0;
        for (RwOpenMode m : modes)
            mask |= m.pflag;
        return mask;
    }

    private static Class getNativeClass() {
        try {
            Class ret = Class.forName("net.schmizz.sshj.sftp.OpenMode");
            return ret;
        } catch (ClassNotFoundException e) {
            RwSentry.get().captureException(e, true);
        }
        return null;
    }

    public Object toNative() {
        try {
            Field retField = this.NativeClass.getField(this.toString());
            Object ret = retField.get(this.NativeClass);
            return ret;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            RwSentry.get().captureException(e, true);
        }
        throw new RuntimeException("Native conversion error");
    }
}