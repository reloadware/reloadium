package rw.tests.fixtures;

import rw.handler.RunConfHandler;

import static org.mockito.Mockito.spy;


public class HighlightManagerFixture {
    RunConfHandler handler;

    public HighlightManagerFixture(RunConfHandler handler) throws Exception {
        this.handler = handler;
    }

    public void start() {
        this.handler.__setErrorHighlightManager(spy(this.handler.getErrorHighlightManager()));
    }

    public void stop() {
    }
}