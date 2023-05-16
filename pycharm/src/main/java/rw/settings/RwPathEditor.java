package rw.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.projectRoots.ui.PathEditor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RwPathEditor extends PathEditor {
    public RwPathEditor() {
        super(new FileChooserDescriptor(
                true,
                true,
                false,
                false,
                false,
                true));
        this.setEnabled(true);
    }

    public void addPaths(List<String> paths) {
        this.clearList();
        for (String path : paths) {
            VirtualFile file = new VirtualFileWrapper(new File(path)).getVirtualFile(false);
            super.addPaths(file);
        }
    }

    public List<String> getPaths() {
        List<String> paths = new ArrayList<>();
        for (VirtualFile virtualFile : this.getRoots()) {
            paths.add(virtualFile.toNioPath().toString());
        }
        return paths;
    }
}