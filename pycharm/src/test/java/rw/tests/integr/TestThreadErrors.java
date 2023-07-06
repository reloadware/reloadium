package rw.tests.integr;

import com.intellij.testFramework.EdtTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rw.session.events.ThreadErrorEvent;
import rw.stack.ThreadError;
import rw.stack.ThreadErrorManager;
import rw.tests.BaseTestCase;
import rw.tests.fixtures.CakeshopFixture;
import rw.tests.fixtures.CurrentDebugHandlerFixture;
import rw.tests.fixtures.DebugPythonRunConfHandlerFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class TestThreadErrors extends BaseTestCase {
    CakeshopFixture cakeshop;
    CurrentDebugHandlerFixture currentDebugHandlerFixture;
    DebugPythonRunConfHandlerFixture handlerFixture;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        this.cakeshop = new CakeshopFixture(this.f);
        this.cakeshop.setUp();

        this.handlerFixture = new DebugPythonRunConfHandlerFixture(this.getProject(), this.cakeshop.getRunConf());
        this.currentDebugHandlerFixture = new CurrentDebugHandlerFixture(this.getProject(),
                this.handlerFixture.getHandler());

        this.handlerFixture.setUp();
        this.currentDebugHandlerFixture.setUp();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.cakeshop.tearDown();
        this.handlerFixture.tearDown();
        this.currentDebugHandlerFixture.tearDown();
        this.dialogFactoryFixture.tearDown();
        super.tearDown();
    }

    @Test
    public void testSwitching() {
        ThreadErrorManager manager = spy(new ThreadErrorManager(this.getProject()));
        manager.onSessionStarted(this.handlerFixture.getDebugSession());

        String threadId = "CakeshopThread";

        this.f.configureByText("cakeshop.py", "content = True");

        ThreadErrorEvent event = new ThreadErrorEvent(
                this.f.getFile().getVirtualFile().getPath(),
                this.f.getFile().getVirtualFile(),
                2,
                "Error msg",
                threadId,
                1L
        );
        event.setHandler(this.handlerFixture.getHandler());

        when(manager.getActiveThread()).thenReturn(threadId);

        manager.onThreadError(event);
        ThreadError error = manager.getActiveError();
        assertThat(error.isActive()).isTrue();

        manager.onThreadChanged("new_thread");
        assertThat(error.isActive()).isFalse();
    }
}
