package rw.tests.fixtures;

import rw.consts.Const;

import java.io.File;
import java.nio.file.Files;


public class PackageFixture {
    public File currentVersionFile;
    public File packageDir;
    public File packageDistInfoDir;

    public PackageFixture(String version) throws Exception {
        this.currentVersionFile = new File(String.valueOf(Const.get().getPackagesRootDir()), "version.txt");

        for (String v : Const.singleton.supportedVersions) {
            this.packageDir = new File(Const.get().getPackagePythonVersionDir(v).toString(), Const.get().packageName);
            this.packageDistInfoDir = new File(Const.get().getPackagePythonVersionDir(v).toString(),
                    String.format("%s-%s.dist-info", Const.get().packageName, version));

            packageDir.mkdirs();
            packageDistInfoDir.mkdirs();

            currentVersionFile.toPath().getParent().toFile().mkdir();
            currentVersionFile.createNewFile();
        }
        Files.writeString(currentVersionFile.toPath(), version);
    }
}
