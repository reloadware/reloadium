package rw.tests.unit;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import rw.handler.runConf.PythonRunConfHandler;
import rw.handler.runConf.RunConfHandlerFactory;
import rw.session.*;
import rw.tests.BaseMockedTestCase;
import rw.tests.fixtures.CakeshopFixture;
import rw.tests.fixtures.DialogFactoryFixture;
import rw.tests.fixtures.HighlightManagerFixture;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestSession extends BaseMockedTestCase {
    CakeshopFixture cakeshop;
    DialogFactoryFixture dialogFactoryFixture;
    HighlightManagerFixture highlightManagerFixture;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        this.cakeshop = new CakeshopFixture(this.getProject());
        this.cakeshop.start();

        this.dialogFactoryFixture = new DialogFactoryFixture(this.getProject());
        this.dialogFactoryFixture.start();

        this.highlightManagerFixture = new HighlightManagerFixture();
        this.highlightManagerFixture.start();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.cakeshop.stop();
        this.dialogFactoryFixture.stop();
        this.highlightManagerFixture.stop();

        super.tearDown();
    }

    @Test
    public void testHandshake() {
        PythonRunConfHandler handler = (PythonRunConfHandler) RunConfHandlerFactory.factory(cakeshop.getRunConf());
        Session session = new Session(this.getProject(), handler);

        Event event = session.eventFactory("Handshake\t0.8.5");
        assertThat(event).isInstanceOf(Handshake.class);
        event.handle();
    }

    @Test
    public void testUserError() throws IOException {
        PythonRunConfHandler handler = (PythonRunConfHandler) RunConfHandlerFactory.factory(cakeshop.getRunConf());
        Session session = new Session(this.getProject(), handler);

        File file = new File(this.cakeshop.getRoot().toString(), "cakeshop.py");
        FileUtils.write(file, "1\n2\n3\n4", "utf-8");
        file.createNewFile();

        Event event = session.eventFactory(String.format("UserError\t%s\t2", file.getAbsolutePath()));
        assertThat(event).isInstanceOf(UserError.class);
        event.handle();
        verify(this.dialogFactoryFixture.dialogFactory, times(1)).showFirstUserErrorDialog(this.getProject());
        verify(this.highlightManagerFixture.highlightManager, times(1)).add(
                eq(this.getProject()), eq(file), eq(2));
    }

    @Test
    public void testFrameError() throws IOException {
        PythonRunConfHandler handler = (PythonRunConfHandler) RunConfHandlerFactory.factory(cakeshop.getRunConf());
        Session session = new Session(this.getProject(), handler);

        File file = new File(this.cakeshop.getRoot().toString(), "cakeshop.py");
        FileUtils.write(file, "1\n2\n3\n4", "utf-8");
        file.createNewFile();

        Event event = session.eventFactory(String.format("FrameError\t%s\t2", file.getAbsolutePath()));
        assertThat(event).isInstanceOf(FrameError.class);
        event.handle();
        verify(this.dialogFactoryFixture.dialogFactory, times(1)).showFirstFrameErrorDialog(this.getProject());

        verify(this.highlightManagerFixture.highlightManager, times(1)).add(
        eq(this.getProject()), eq(file), eq(2));
    }

    @Test
    public void testUpdateModule() throws IOException {
        PythonRunConfHandler handler = (PythonRunConfHandler) RunConfHandlerFactory.factory(cakeshop.getRunConf());
        Session session = new Session(this.getProject(), handler);

        File file = new File(this.cakeshop.getRoot().toString(), "cakeshop.py");
        FileUtils.write(file, "1\n2\n3\n4", "utf-8");
        file.createNewFile();

        Event event = session.eventFactory(String.format("UpdateModule\t%s", file.getAbsolutePath()));
        assertThat(event).isInstanceOf(UpdateModule.class);
        event.handle();

        verify(this.highlightManagerFixture.highlightManager, times(1)).clearFile(eq(file));
    }

    @Test
    public void testUpdateFrame() throws IOException {
        PythonRunConfHandler handler = (PythonRunConfHandler) RunConfHandlerFactory.factory(cakeshop.getRunConf());
        Session session = new Session(this.getProject(), handler);

        File file = new File(this.cakeshop.getRoot().toString(), "cakeshop.py");
        FileUtils.write(file, "1\n2\n3\n4", "utf-8");
        file.createNewFile();

        Event event = session.eventFactory(String.format("UpdateFrame\t%s", file.getAbsolutePath()));
        assertThat(event).isInstanceOf(UpdateFrame.class);
        event.handle();
    }
}