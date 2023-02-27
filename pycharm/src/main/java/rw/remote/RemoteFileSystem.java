package rw.remote;

import rw.audit.RwSentry;
import rw.pkg.FileSystem;
import rw.remote.sftp.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

public class RemoteFileSystem extends FileSystem {
    SFTPClient sftp;

    public RemoteFileSystem(SFTPClient sftp) {
        this.sftp = sftp;
    }

    @Override
    public String readString(File path) throws IOException {
        try {
            FileAttributes fileAttributes = this.sftp.stat(path.toString());
            RemoteFile file = this.sftp.open(path.toString(), Set.of(RwOpenMode.READ));
            int fileSize = (int) fileAttributes.getSize();

            byte[] bytes = new byte[fileSize];
            file.read(0, bytes, 0, fileSize);

            String ret = new String(bytes, StandardCharsets.UTF_8);
            return ret;
        } catch (SFTPException e) {
            throw new IOException();
        }
    }

    @Override
    public void writeString(File path, String content) {
        try {
            RemoteFile file = this.sftp.open(path.toString(), Set.of(RwOpenMode.WRITE, RwOpenMode.CREAT));
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            int fileSize = bytes.length;

            file.write(0, bytes, 0, fileSize);
        } catch (SFTPException e) {
            RwSentry.get().captureException(e, true);
        }
    }

    @Override
    public void putFile(File src, File dst) {
        try {
            this.sftp.put(src.toString(), dst.toString());
        } catch (SFTPException e) {
            RwSentry.get().captureException(e, true);
        }
    }

    @Override
    public void putDirectory(File src, File dst) {
        try {
            // Remove Dir
            this.sftp.rename(dst.toString(), "/tmp/" + UUID.randomUUID());
        } catch (SFTPException ignored) {
        }
        try {
            this.sftp.mkdirs(dst.toString());
            this.sftp.put(src.toString(), dst.toString());
        } catch (SFTPException e) {
            RwSentry.get().captureException(e, true);
        }
    }

    @Override
    public File getHome() {
        return new File("/root");
    }
}
