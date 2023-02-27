package rw.pkg;

import org.apache.commons.io.FileUtils;
import rw.consts.Const;
import rw.util.Architecture;

import java.io.File;
import java.io.IOException;

abstract public class FileSystem {
    public abstract String readString(File path) throws IOException;
    public abstract void writeString(File path, String content) throws IOException;
    public abstract void putFile(File src, File dst) throws IOException;
    public abstract void putDirectory(File src, File dst) throws IOException;


    abstract public File getHome();

    public File getDotDir() {
        return new File(this.getHome().toString(), this.getDotDirName());
    }

    public File getConfigFile() {
        return new File(this.getDotDir().toString(), Const.get().configFilename);
    }

    private String getDotDirName() {
        return "." + Const.get().packageName;
    }

    public File getPackagesRootDir() {
        return new File(this.getDotDir().toString(), Const.get().packageDirName);
    }

    public File getPackagePythonVersionDir(String version) {
        return new File(this.getPackagesRootDir().toString(), version);
    }
}
