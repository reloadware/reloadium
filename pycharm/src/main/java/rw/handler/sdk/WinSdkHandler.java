package rw.handler.sdk;

import com.intellij.openapi.projectRoots.Sdk;
import rw.config.Config;
import rw.pkg.Architecture;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class WinSdkHandler extends BaseSdkHandler {
    public WinSdkHandler(Sdk sdk) {
        super(sdk);
    }

    public File getPackageDir() {
        if (!this.isSdkVersionSupported()) {
            return Config.get().getPackagePythonVersionDir(Config.get().supportedVersions[0]).toFile();
        }

        assert this.sdk.getHomePath() != null;
        try {
            String interpreterContent = new String(Files.readAllBytes(Path.of(this.sdk.getHomePath())));
            if (interpreterContent.contains("amd64")) {
                return Config.get().getPackagePythonVersionDir(this.getVersion(), Architecture.x64).toFile();
            }
            else {
                return Config.get().getPackagePythonVersionDir(this.getVersion(), Architecture.x86).toFile();
            }
        } catch (IOException e) {
            return Config.get().getPackagePythonVersionDir(this.getVersion()).toFile();
        }
    };
}
