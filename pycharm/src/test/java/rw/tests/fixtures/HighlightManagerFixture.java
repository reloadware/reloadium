package rw.tests.fixtures;

import com.intellij.openapi.project.Project;
import rw.handler.runConf.BaseRunConfHandler;
import rw.highlights.ErrorHighlightManager;

import static org.mockito.Mockito.*;


public class HighlightManagerFixture {
    public ErrorHighlightManager errorHighlightManager;

    BaseRunConfHandler handler;
    public HighlightManagerFixture(BaseRunConfHandler handler) throws Exception {
        this.handler = handler;
    }

    public void start() {
        this.handler.__setErrorHighlightManager(spy(this.handler.getErrorHighlightManager()));
    }

    public void stop() {
    }
}