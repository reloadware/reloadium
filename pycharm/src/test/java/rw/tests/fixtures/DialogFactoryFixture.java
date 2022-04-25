package rw.tests.fixtures;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;
import rw.dialogs.DialogFactory;
import rw.dialogs.TipDialog;

import javax.swing.*;

import static org.mockito.Mockito.*;


public class DialogFactoryFixture {
    public DialogFactory dialogFactory;
    private Project project;

    public DialogFactoryFixture(Project project) throws Exception {
        this.project = project;
    }

    public void start() {
        this.dialogFactory = mock(DialogFactory.class);
        lenient().doReturn(true).when(this.dialogFactory).showFirstRunDialog(any());
        lenient().doReturn(true).when(this.dialogFactory).showFirstDebugDialog(any());
        DialogFactory.singleton = this.dialogFactory;
    }

    public void stop() {
    }
}