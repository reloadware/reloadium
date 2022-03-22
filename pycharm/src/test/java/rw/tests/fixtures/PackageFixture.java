package rw.tests.fixtures;

import org.apache.maven.artifact.versioning.ComparableVersion;
import rw.config.Config;

import java.io.File;
import java.nio.file.Files;


public class PackageFixture {
    public File currentVersionFile;
    public File packageDir;
    public File packageDistInfoDir;

    public PackageFixture(String version) throws Exception {
        this.currentVersionFile = new File(String.valueOf(Config.get().getPackagesRootDir()), "version.txt");

        for (String v : Config.singleton.supportedVersions) {
            this.packageDir = new File(Config.get().getPackagePythonVersionDir(v).toString(), Config.get().packageName);
            this.packageDistInfoDir = new File(Config.get().getPackagePythonVersionDir(v).toString(),
                    String.format("%s-%s.dist-info", Config.get().packageName, version));

            packageDir.mkdirs();
            packageDistInfoDir.mkdirs();

            currentVersionFile.toPath().getParent().toFile().mkdir();
            currentVersionFile.createNewFile();
        }
        Files.writeString(currentVersionFile.toPath(), version);
    }
}
