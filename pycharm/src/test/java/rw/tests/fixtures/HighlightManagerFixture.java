package rw.tests.fixtures;

import rw.handler.BaseRunConfHandler;

import static org.mockito.Mockito.*;


public class HighlightManagerFixture {
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