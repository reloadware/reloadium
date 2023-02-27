package rw.remote.sftp;

import rw.audit.RwSentry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RemoteFile {
    Object device;

    public RemoteFile(Object device) {
        this.device = device;
    }

    public void write(long fileOffset, byte[] data, int offset, int len) {
        try {
            Method write = this.device.getClass().getMethod("write", long.class, byte[].class, int.class, int.class);
            write.invoke(this.device, fileOffset, data, offset, len);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            RwSentry.get().captureException(e, true);
        }
    }

    public void read(long fileOffset, byte[] to, int offset, int len) {
        try {
            Method read = this.device.getClass().getMethod("read", long.class, byte[].class, int.class, int.class);
            read.invoke(this.device, fileOffset, to, offset, len);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            RwSentry.get().captureException(e, true);
        }
    }
}
