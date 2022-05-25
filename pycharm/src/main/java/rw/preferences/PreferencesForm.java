package rw.preferences;

import com.intellij.openapi.ui.ComboBox;
import rw.icons.Icons;
import rw.util.colormap.ColorMaps;

import javax.swing.*;

public class PreferencesForm {
    private JPanel mainPanel;
    private JSpinner blinkDuration;
    private JComboBox<ImageIcon> timingColormap;

    private void createUIComponents() {
        this.blinkDuration = new JSpinner(new SpinnerNumberModel(0, 0, 2000, 10));
        this.timingColormap = new ComboBox<>();

        this.timingColormap.setModel(new DefaultComboBoxModel(ColorMaps.get().getAllImages().toArray()));
    }

    public JPanel getMainPanel() {
        return this.mainPanel;
    }

    public PreferencesState getState() {
        PreferencesState state = new PreferencesState();
        state.blinkDuration = ((SpinnerNumberModel)this.blinkDuration.getModel()).getNumber().intValue();
        state.timingColorMap = ColorMaps.get().getColorMapByImage((ImageIcon) this.timingColormap.getModel().getSelectedItem()).getName();
        return state;
    }

    public void setState(PreferencesState state) {
        this.blinkDuration.setValue(state.blinkDuration);
        this.timingColormap.getModel().setSelectedItem(ColorMaps.get().getColorMapByName(state.timingColorMap).getImage());
    }
}
