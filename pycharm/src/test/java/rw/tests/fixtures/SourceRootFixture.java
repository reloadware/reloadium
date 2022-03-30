package rw.tests.fixtures;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.testFramework.PsiTestUtil;
import com.jetbrains.python.sdk.PythonSdkType;
import rw.util.OsType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class SourceRootFixture {
    protected VirtualFile srcRoot;
    private Module module;

    public SourceRootFixture(Module module) throws IOException {
        this.module = module;
    }

    public void start() throws Exception {
        this.srcRoot = new VirtualFileWrapper(Files.createTempDirectory("srcRoot").toFile()).getVirtualFile();
        PsiTestUtil.addSourceRoot(this.module, this.srcRoot, false);
    }

    public void stop() {
        if (this.srcRoot != null)
            PsiTestUtil.removeSourceRoot(this.module, this.srcRoot);
    }

    public VirtualFile getSrcRoot() {
        return this.srcRoot;
    }
}
