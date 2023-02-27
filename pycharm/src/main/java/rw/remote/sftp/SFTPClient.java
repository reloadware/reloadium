package rw.remote.sftp;

import rw.audit.RwSentry;
import rw.remote.RemoteUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class SFTPClient {
    Object device;

    public SFTPClient(Object device) {
        this.device = device;
    }

    public FileAttributes stat(String path) throws SFTPException {
        try {
            Method method = this.device.getClass().getMethod("stat", String.class);
            FileAttributes ret = new FileAttributes(method.invoke(this.device, path));
            return ret;
        } catch (InvocationTargetException e) {
            RemoteUtils.checkSftpException(e);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            RwSentry.get().captureException(e, true);
        }
        throw new RuntimeException("Could not stat remote file " + path);
    }

    public FileMode.Type type(String path) throws SFTPException {
        try {
            Method method = this.device.getClass().getMethod("type", String.class);
            FileMode ret = new FileMode((Integer) method.invoke(this.device, path));
            return ret.getType();
        } catch (InvocationTargetException e) {
            RemoteUtils.checkSftpException(e);
        } catch (Exception e) {
            RwSentry.get().captureException(e, true);
        }
        throw new RuntimeException("Could not type remote file " + path);
    }

    public void put(String src, String dst) throws SFTPException {
        try {
            Method method = this.device.getClass().getMethod("put", String.class, String.class);
            method.invoke(this.device, src, dst);
        } catch (InvocationTargetException e) {
            RemoteUtils.checkSftpException(e);
        } catch (Exception e) {
            RwSentry.get().captureException(e, true);
        }
    }

    public void rename(String src, String dst) throws SFTPException {
        try {
            Method method = this.device.getClass().getMethod("rename", String.class, String.class);
            method.invoke(this.device, src, dst);
        } catch (InvocationTargetException e) {
            RemoteUtils.checkSftpException(e);
        } catch (Exception e) {
            RwSentry.get().captureException(e, true);
        }
    }

    public void mkdirs(String dir) throws SFTPException {
        try {
            Method method = this.device.getClass().getMethod("mkdirs", String.class);
            method.invoke(this.device, dir);
        } catch (InvocationTargetException e) {
            RemoteUtils.checkSftpException(e);
        } catch (Exception e) {
            RwSentry.get().captureException(e, true);
        }
    }

    public RemoteFile open(String path, Set<RwOpenMode> modes) throws SFTPException {
        try {
            Method method = this.device.getClass().getMethod("open", String.class, Set.class);

            Set<Object> nativeModes = new HashSet<>();
            for (RwOpenMode m : modes) {
                nativeModes.add(m.toNative());
            }

            RemoteFile ret = new RemoteFile(method.invoke(this.device, path, nativeModes));
            return ret;
        } catch (InvocationTargetException e) {
            RemoteUtils.checkSftpException(e);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            RwSentry.get().captureException(e, true);
        }
        throw new RuntimeException("Could not open remote file " + path);
    }
}
