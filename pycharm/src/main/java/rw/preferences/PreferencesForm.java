package rw.preferences;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.BrowserHyperlinkListener;
import rw.quickconfig.CumulateType;
import rw.quickconfig.ErrorHandlingMode;
import rw.quickconfig.ProfilerType;
import rw.util.colormap.ColorMaps;

import javax.swing.*;

public class PreferencesForm {
    private JPanel mainPanel;
    private JSpinner blinkDuration;
    private JComboBox<ImageIcon> timingColormap;
    private JCheckBox markReloadable;
    private JComboBox<ProfilerType> defaultProfiler;
    private JComboBox<CumulateType> defaultCumulateType;
    private JComboBox<ErrorHandlingMode> errorHandlingMode;
    private JCheckBox alwaysCollectMemory;
    private JCheckBox runtimeCompletion;
    private JPanel Miscellaneous;

    private void createUIComponents() {
        this.blinkDuration = new JSpinner(new SpinnerNumberModel(0, 0, 2000, 10));
        this.timingColormap = new ComboBox<>();
        this.timingColormap.setModel(new DefaultComboBoxModel(ColorMaps.get().getAllImages().toArray()));

        this.defaultProfiler = new ComboBox<>();
        this.defaultProfiler.setModel(new DefaultComboBoxModel(ProfilerType.getAll()));
        this.defaultProfiler.setSelectedItem(ProfilerType.DEFAULT);

        this.defaultCumulateType = new ComboBox<>();
        this.defaultCumulateType.setModel(new DefaultComboBoxModel(CumulateType.getAll()));
        this.defaultCumulateType.setSelectedItem(CumulateType.DEFAULT);

        this.errorHandlingMode = new ComboBox<>();
        this.errorHandlingMode.setModel(new DefaultComboBoxModel(ErrorHandlingMode.getAll()));
        this.errorHandlingMode.setSelectedItem(ErrorHandlingMode.DEFAULT);
    }

    public JPanel getMainPanel() {
        return this.mainPanel;
    }

    public PreferencesState getState() {
        PreferencesState state = new PreferencesState();
        state.blinkDuration = ((SpinnerNumberModel) this.blinkDuration.getModel()).getNumber().intValue();
        state.timingColorMap = ColorMaps.get().getColorMapByImage((ImageIcon) this.timingColormap.getModel().getSelectedItem()).getName();
        state.alwaysCollectMemory = this.alwaysCollectMemory.isSelected();
        state.markReloadable = this.markReloadable.isSelected();
        state.defaultProfiler = (ProfilerType) this.defaultProfiler.getModel().getSelectedItem();
        state.defaultCumulateType = (CumulateType) this.defaultCumulateType.getModel().getSelectedItem();
        state.defaultErrorHandlingMode = (ErrorHandlingMode) this.errorHandlingMode.getModel().getSelectedItem();
        state.runtimeCompletion = this.runtimeCompletion.isSelected();
        return state;
    }

    public void setState(PreferencesState state) {
        this.blinkDuration.setValue(state.blinkDuration);
        this.timingColormap.getModel().setSelectedItem(ColorMaps.get().getColorMapByName(state.timingColorMap).getImage());
        this.alwaysCollectMemory.setSelected(state.alwaysCollectMemory);
        this.markReloadable.setSelected(state.markReloadable);
        this.defaultProfiler.getModel().setSelectedItem(state.defaultProfiler);
        this.defaultCumulateType.getModel().setSelectedItem(state.defaultCumulateType);
        this.errorHandlingMode.getModel().setSelectedItem(state.defaultErrorHandlingMode);
        this.runtimeCompletion.setSelected(state.runtimeCompletion);
    }
}
