package rw.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.projectRoots.ui.PathEditor;

public class ReloadiumPath extends PathEditor {
  public ReloadiumPath(FileChooserDescriptor descriptor) {
    super(descriptor);
    this.setEnabled(true);
  }
}
