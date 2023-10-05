package rw.quickconfig;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.ui.ComboBox;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Map;


public class QuickConfig {
    QuickConfigCallback onQuickConfigChange;
    private JComboBox<String> profiler;
    private JPanel main;
    private JCheckBox frameScope;
    private JLabel setProfilerAsDefault;
    private JLabel setFrameScopeAsDefault;
    private JComboBox<String> cumulateType;
    private JLabel setCumulateTypeAsDefault;
    private JPanel Other;
    private JComboBox errorHandlingMode;
    private JLabel setErrorHandlingModeAsDefault;
    private JCheckBox alwaysCollectMemory;
    private JLabel setAlwaysCollectMemoryAsDefault;

    public QuickConfig(QuickConfigCallback onQuickConfigChange) {
        this.onQuickConfigChange = onQuickConfigChange;
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            this.createUIComponents();
        }
    }

    private void createUIComponents() {
        Preferences preferences = Preferences.get();
        QuickConfig This = this;

        QuickConfigState state = QuickConfigStateFactory.create();

        // Profiler
        this.profiler = new ComboBox<>();
        this.profiler.setModel(new DefaultComboBoxModel(ProfilerType.getAll()));
        this.profiler.setSelectedItem(state.getProfiler());

        this.setProfilerAsDefault = this.setAsDefaultFactory(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PreferencesState state = preferences.getState();
                state.defaultProfiler = This.getState().getProfiler();
                Preferences.get().loadState(state);
            }
        });

        // Frame Scope
        this.setFrameScopeAsDefault = this.setAsDefaultFactory(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PreferencesState state = preferences.getState();
                state.defaultFrameScope = This.getState().getFrameScope();
                Preferences.get().loadState(state);
            }
        });

        this.frameScope = new JCheckBox();
        this.frameScope.setSelected(state.getFrameScope());

        // Always Collect Memory
        this.alwaysCollectMemory = new JCheckBox();
        this.alwaysCollectMemory.setSelected(state.getAlwaysCollectMemory());

        this.setAlwaysCollectMemoryAsDefault = this.setAsDefaultFactory(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PreferencesState state = preferences.getState();
                state.alwaysCollectMemory = This.getState().getAlwaysCollectMemory();
                Preferences.get().loadState(state);
            }
        });

        // Cumulate values
        this.setCumulateTypeAsDefault = this.setAsDefaultFactory(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PreferencesState state = preferences.getState();
                state.defaultCumulateType = This.getState().getComulateType();
                Preferences.get().loadState(state);
            }
        });

        this.cumulateType = new ComboBox<>();
        this.cumulateType.setModel(new DefaultComboBoxModel(CumulateType.getAll()));
        this.cumulateType.setSelectedItem(state.getComulateType());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                This.onQuickConfigChange.onChange(This.getState());
            }
        };

        // ErrorHandlingMode
        this.setErrorHandlingModeAsDefault = this.setAsDefaultFactory(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PreferencesState state = preferences.getState();
                state.defaultErrorHandlingMode = This.getState().getErrorHandlingMode();
                Preferences.get().loadState(state);
            }
        });

        this.errorHandlingMode = new ComboBox<>();
        this.errorHandlingMode.setModel(new DefaultComboBoxModel(ErrorHandlingMode.getAll()));
        this.errorHandlingMode.setSelectedItem(state.getErrorHandlingMode());

        this.frameScope.addActionListener(actionListener);
        this.alwaysCollectMemory.addActionListener(actionListener);
        this.profiler.addActionListener(actionListener);
        this.cumulateType.addActionListener(actionListener);
        this.errorHandlingMode.addActionListener(actionListener);
    }

    public JPanel getContent() {
        return this.main;
    }

    public QuickConfigState getState() {
        QuickConfigState state = new QuickConfigState((ProfilerType) this.profiler.getModel().getSelectedItem(),
                this.frameScope.isSelected(), (CumulateType) this.cumulateType.getModel().getSelectedItem(),
                (ErrorHandlingMode) this.errorHandlingMode.getModel().getSelectedItem(),
                this.alwaysCollectMemory.isSelected());
        return state;
    }

    private JLabel setAsDefaultFactory(MouseAdapter mouseAdapter) {
        JLabel ret = new JLabel();
        ret.setText("Set as default");
        ret.setForeground(CodeInsightColors.HYPERLINK_ATTRIBUTES.getDefaultAttributes().getForegroundColor());
        ret.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ret.addMouseListener(mouseAdapter);

        ret.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPopupMenu popup = new JPopupMenu();

                JMenuItem item = new JMenuItem("Done!");
                popup.add(item);

                popup.show(ret, e.getX() + 10, e.getY() - 30);

                Timer timer = new Timer(2000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        popup.setVisible(false);
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        Map attributes = ret.getFont().getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        Font underlinedFont = new Font(attributes);
        ret.setFont(underlinedFont);

        return ret;
    }
}