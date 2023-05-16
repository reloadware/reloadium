package rw.tests.fixtures;

import rw.pkg.PackageManager;
import rw.pkg.wheel.BaseWheel;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;


public class PackageFixture {
    public File currentVersionFile;
    public Map<String, File> pythonVersionToPackageDirs;

    public PackageFixture(PackageManager packageManager, String version) throws Exception {
        this.currentVersionFile = new File(String.valueOf(packageManager.getFs().getPackagesRootDir()), "version.txt");

        this.pythonVersionToPackageDirs = new HashMap<>();

        for (BaseWheel wheel : packageManager.getWheels()) {
            File pythonVersionDir = new File(packageManager.getFs().getPackagesRootDir(), wheel.getDstDirName());
            pythonVersionDir.mkdirs();
            this.pythonVersionToPackageDirs.put(wheel.getVersion(), pythonVersionDir);
        }

        this.currentVersionFile.toPath().getParent().toFile().mkdir();
        this.currentVersionFile.createNewFile();
        Files.writeString(this.currentVersionFile.toPath(), version);
    }
}
