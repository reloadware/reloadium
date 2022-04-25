package rw.tests.fixtures;

import com.intellij.openapi.project.Project;
import rw.dialogs.DialogFactory;
import rw.session.HighlightManager;

import static org.mockito.Mockito.*;


public class HighlightManagerFixture {
    public HighlightManager highlightManager;

    public HighlightManagerFixture() throws Exception {
    }

    public void start() {
        this.highlightManager = spy(new HighlightManager());
        HighlightManager.singleton = this.highlightManager;
    }

    public void stop() {
    }
}