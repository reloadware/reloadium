package rw.settings;

import javax.swing.*;

public class ProjectSettingsForm {
    private JCheckBox addCurrentWorkingDirectoryCB;
    private JPanel mainPanel;
    private JComponent reloadiumPathPanel;
    private JCheckBox printLogoCB;
    private JCheckBox cacheEnabledCB;
    private JCheckBox verboseCB;
    private JCheckBox watchSourceRootsCB;
    private JComponent reloadiumIgnorePanel;
    private RwPathEditor reloadiumPath;
    private RwPathEditor reloadiumIgnore;

    private void createUIComponents() {
        this.reloadiumPath = new RwPathEditor();
        this.reloadiumIgnore = new RwPathEditor();

        this.reloadiumPathPanel = this.reloadiumPath.createComponent();
        this.reloadiumIgnorePanel = this.reloadiumIgnore.createComponent();
    }

    public JPanel getMainPanel() {
        return this.mainPanel;
    }

    public ProjectState getState() {
        ProjectState state = new ProjectState();

        state.reloadiumPath = this.reloadiumPath.getPaths();
        state.reloadiumIgnore = this.reloadiumIgnore.getPaths();
        state.watchCwd = this.addCurrentWorkingDirectoryCB.isSelected();
        state.watchSourceRoots = this.watchSourceRootsCB.isSelected();
        state.printLogo = this.printLogoCB.isSelected();
        state.cache = this.cacheEnabledCB.isSelected();
        state.verbose = this.verboseCB.isSelected();
        return state;
    }

    public void setState(ProjectState state) {
        this.reloadiumPath.addPaths(state.reloadiumPath);
        this.reloadiumIgnore.addPaths(state.reloadiumIgnore);

        this.addCurrentWorkingDirectoryCB.setSelected(state.watchCwd);
        this.watchSourceRootsCB.setSelected(state.watchSourceRoots);
        this.printLogoCB.setSelected(state.printLogo);
        this.cacheEnabledCB.setSelected(state.cache);
        this.verboseCB.setSelected(state.verbose);
    }
}
