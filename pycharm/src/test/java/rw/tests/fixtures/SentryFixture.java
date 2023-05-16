package rw.tests.fixtures;

import rw.audit.RwSentry;

import static org.mockito.Mockito.*;


public class SentryFixture {
    public RwSentry mocked;

    public SentryFixture() throws Exception {
        RwSentry.singleton = spy(RwSentry.get());
        this.mocked = RwSentry.singleton;

        lenient().doNothing().when(RwSentry.singleton).captureError(any());
    }
}
