package rw.tests.fixtures;

import com.intellij.openapi.project.Project;
import rw.dialogs.DialogFactory;

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