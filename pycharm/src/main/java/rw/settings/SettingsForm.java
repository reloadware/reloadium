package rw.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingsForm {
    private JCheckBox addCurrentWorkingDirectoryCB;
    private JPanel mainPanel;
    private JComponent reloadiumPathPanel;
    private JCheckBox printLogoCB;
    private JCheckBox cacheEnabledCB;
    private JCheckBox verboseCB;
    private JCheckBox debuggerSpeedupsCB;
    private JCheckBox watchSourceRootsCB;
    private ReloadiumPath reloadiumPath;

    private void createUIComponents() {
        this.reloadiumPath = new ReloadiumPath(new FileChooserDescriptor(
                true,
                true,
                false,
                false,
                false,
                true));

        this.reloadiumPathPanel = this.reloadiumPath.createComponent();
    }

    public JPanel getMainPanel() {
        return this.mainPanel;
    }

    public PluginState getState() {
        PluginState state = new PluginState();

        List<String> paths = new ArrayList<>();
        for (VirtualFile virtualFile : this.reloadiumPath.getRoots()) {
            paths.add(virtualFile.toNioPath().toString());
        }

        state.reloadiumPath = paths;
        state.watchCwd = this.addCurrentWorkingDirectoryCB.isSelected();
        state.watchSourceRoots = this.watchSourceRootsCB.isSelected();
        state.printLogo = this.printLogoCB.isSelected();
        state.cacheEnabled = this.cacheEnabledCB.isSelected();
        state.verbose = this.verboseCB.isSelected();
        state.debuggerSpeedups = this.debuggerSpeedupsCB.isSelected();
        return state;
    }

    public void setState(PluginState state) {
        this.reloadiumPath.clearList();
        for (String path: state.reloadiumPath) {
            VirtualFile file = new VirtualFileWrapper(new File(path)).getVirtualFile(false);
            this.reloadiumPath.addPaths(file);
        }
        this.addCurrentWorkingDirectoryCB.setSelected(state.watchCwd);
        this.watchSourceRootsCB.setSelected(state.watchSourceRoots);
        this.printLogoCB.setSelected(state.printLogo);
        this.cacheEnabledCB.setSelected(state.cacheEnabled);
        this.verboseCB.setSelected(state.verbose);
        this.debuggerSpeedupsCB.setSelected(state.debuggerSpeedups);
    }
}
