package rw.remote.sftp;

import rw.audit.RwSentry;
import rw.remote.RemoteUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FileAttributes {
    Object device;

    public FileAttributes(Object device) {
        this.device = device;
    }

    public long getSize() throws IOException {
        try {
            Method stat = this.device.getClass().getMethod("getSize");
            long ret = (long) stat.invoke(this.device);
            return ret;
        }
        catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            throw new IOException();
        }
    }
}
