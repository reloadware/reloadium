package rw.pkg;

import rw.consts.Const;

import java.io.File;
import java.io.IOException;

abstract public class FileSystem {
    static final String DOT_DIR_NAME = "." + Const.get().packageName;

    public abstract String readString(File path) throws IOException;

    public abstract void writeString(File path, String content) throws IOException;

    public abstract void putFile(File src, File dst) throws IOException;

    public abstract void putDirectory(File src, File dst) throws IOException;

    abstract public File getHome();

    public File getDotDir() {
        return new File(this.getHome().toString(), DOT_DIR_NAME);
    }

    public File getConfigFile() {
        return new File(this.getDotDir().toString(), Const.get().configFilename);
    }


    public File getPackagesRootDir() {
        return new File(this.getDotDir().toString(), Const.get().packageDirName);
    }
}
