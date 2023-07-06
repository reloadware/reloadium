package rw.remote.sftp;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;


public class SFTPClient {
    Object device;

    public SFTPClient(Object device) {
        this.device = device;
    }

    public FileAttributes stat(String path) throws IOException {
        try {
            Method method = this.device.getClass().getMethod("stat", String.class);
            FileAttributes ret = new FileAttributes(method.invoke(this.device, path));
            return ret;
        }
        catch (InvocationTargetException e) {
            if(e.getTargetException().getClass().getName().equals("net.schmizz.sshj.sftp.SFTPException") && e.getTargetException().getMessage().contains("no such file or directory")) {
                throw new IOException();
            }
            throw new RuntimeException(e);
        }
        catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            throw new IOException();
        }
    }

    public FileMode.Type type(String path) throws IOException {
        try {
            Method method = this.device.getClass().getMethod("type", String.class);
            FileMode ret = new FileMode((Integer) method.invoke(this.device, path));
            return ret.getType();
        }
        catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            throw new IOException();
        }
    }

    public void put(String src, String dst) throws IOException {
        try {
            Method method = this.device.getClass().getMethod("put", String.class, String.class);
            method.invoke(this.device, src, dst);
        }
        catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            throw new IOException();
        }
    }

    public void rename(String src, String dst) throws IOException {
        try {
            Method method = this.device.getClass().getMethod("rename", String.class, String.class);
            method.invoke(this.device, src, dst);
        }
        catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            throw new IOException();
        }
    }

    public void mkdirs(String dir) throws IOException {
        try {
            Method method = this.device.getClass().getMethod("mkdirs", String.class);
            method.invoke(this.device, dir);
        }
        catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            throw new IOException();
        }
    }

    public RemoteFile open(String path, Set<RwOpenMode> modes) throws IOException {
        try {
            Method method = this.device.getClass().getMethod("open", String.class, Set.class);

            Set<Object> nativeModes = new HashSet<>();
            for (RwOpenMode m : modes) {
                nativeModes.add(m.toNative());
            }

            RemoteFile ret = new RemoteFile(method.invoke(this.device, path, nativeModes));
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
