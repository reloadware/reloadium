package rw.pkg;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class NativeFileSystem extends FileSystem {
    @VisibleForTesting
    public static NativeFileSystem singleton = null;

    @VisibleForTesting
    public NativeFileSystem() {
    }

    public static NativeFileSystem get() {
        if (singleton == null) {
            singleton = new NativeFileSystem();
        }
        return singleton;
    }

    @Override
    public String readString(File path) throws IOException {
        return Files.readString(path.toPath());
    }

    @Override
    public void writeString(File path, String content) throws IOException {
        Files.writeString(path.toPath(), content);
    }

    @Override
    public void putFile(File src, File dst) throws IOException {
        FileUtils.copyFile(src, dst);
    }

    @Override
    public void putDirectory(File src, File dst) throws IOException {
        FileUtils.copyDirectory(src, dst);
    }

    public File getHome() {
        return FileUtils.getUserDirectory();
    }
}
