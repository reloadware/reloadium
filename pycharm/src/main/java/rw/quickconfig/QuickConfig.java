package rw.quickconfig;

import com.intellij.openapi.ui.ComboBox;
import rw.handler.runConf.BaseRunConfHandler;
import rw.session.cmds.QuickConfigCmd;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class QuickConfig {
    private JComboBox<String> profilerCB;
    private JPanel main;
    private BaseRunConfHandler runConfHandler;

    private void createUIComponents() {
        QuickConfigState state = QuickConfigStateFactory.create();
        this.profilerCB = new ComboBox<>();
        this.profilerCB.setModel(new DefaultComboBoxModel(ProfilerType.getAll()));
        this.profilerCB.setSelectedItem(state.profiler);

        QuickConfig This = this;

        this.profilerCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                This.updateState();
            }
        });
    }

    public QuickConfig(BaseRunConfHandler runConfHandler) {
        this.runConfHandler = runConfHandler;
    }

    public JPanel getContent() {
        return this.main;
    }

    public void updateState() {
        QuickConfigState state = this.getState();
        QuickConfigCmd cmd = new QuickConfigCmd(state);
        this.runConfHandler.getSession().send(cmd);
    }

    public QuickConfigState getState() {
        QuickConfigState state = new QuickConfigState();
        state.profiler = (ProfilerType) this.profilerCB.getModel().getSelectedItem();
        return state;
    }
}