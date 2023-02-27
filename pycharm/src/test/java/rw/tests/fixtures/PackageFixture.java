package rw.tests.fixtures;

import rw.consts.Const;
import rw.pkg.PackageManager;

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

        for (String v : Const.singleton.supportedVersions) {
            File packageDir = new File(packageManager.getFs().getPackagePythonVersionDir(v).toString(), Const.get().packageName);
            this.pythonVersionToPackageDirs.put(v, packageDir);

            File packageDistInfoDir = new File(packageManager.getFs().getPackagePythonVersionDir(v).toString(),
                    String.format("%s-%s.dist-info", Const.get().packageName, version));

            packageDir.mkdirs();
            packageDistInfoDir.mkdirs();

            currentVersionFile.toPath().getParent().toFile().mkdir();
            currentVersionFile.createNewFile();
        }
        Files.writeString(currentVersionFile.toPath(), version);
    }
}
