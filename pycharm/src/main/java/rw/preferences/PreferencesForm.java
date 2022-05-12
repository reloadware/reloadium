package rw.preferences;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PreferencesForm {
    private JPanel mainPanel;
    private JSpinner blinkDuration;

    private void createUIComponents() {
        this.blinkDuration = new JSpinner(new SpinnerNumberModel(0, 0, 2000, 10));
    }

    public JPanel getMainPanel() {
        return this.mainPanel;
    }

    public PreferencesState getState() {
        PreferencesState state = new PreferencesState();
        state.blinkDuration = ((SpinnerNumberModel)this.blinkDuration.getModel()).getNumber().intValue();
        return state;
    }

    public void setState(PreferencesState state) {
        this.blinkDuration.setValue(state.blinkDuration);
    }
}
