package rw.remote.sftp;

import rw.audit.RwSentry;
import rw.remote.RemoteUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FileAttributes {
    Object device;

    public FileAttributes(Object device) {
        this.device = device;
    }

    public long getSize() throws SFTPException {
        try {
            Method stat = this.device.getClass().getMethod("getSize");
            long ret = (long) stat.invoke(this.device);
            return ret;

        } catch (InvocationTargetException e) {
            RemoteUtils.checkSftpException(e);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            RwSentry.get().captureException(e, true);
        }
        throw new RuntimeException("Could not get size");
    }
}
