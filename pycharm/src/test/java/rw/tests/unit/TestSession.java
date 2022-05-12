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

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        this.cakeshop = new CakeshopFixture(this.getProject());
        this.cakeshop.start();

        this.dialogFactoryFixture = new DialogFactoryFixture(this.getProject());
        this.dialogFactoryFixture.start();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.cakeshop.stop();
        this.dialogFactoryFixture.stop();

        super.tearDown();
    }

    @Test
    public void testHandshake() {
        PythonRunConfHandler handler = (PythonRunConfHandler) RunConfHandlerFactory.factory(cakeshop.getRunConf());
        Session session = new Session(this.getProject(), handler);

        String payload = String.format("{\"ID\": \"Handshake\", \"VERSION\": \"%s\", \"version\": \"2.3.1\"}",
                Handshake.VERSION);
        Handshake event = (Handshake) session.eventFactory(payload);
        assertThat(event).isInstanceOf(Handshake.class);
        assertThat(event.getVersion()).isEqualTo("2.3.1");

        event.handle();
    }

    @Test
    public void testUserError() throws Exception {
        PythonRunConfHandler handler = (PythonRunConfHandler) RunConfHandlerFactory.factory(cakeshop.getRunConf());
        HighlightManagerFixture highlightManagerFixture = new HighlightManagerFixture(handler);
        highlightManagerFixture.start();

        Session session = new Session(this.getProject(), handler);

        File file = new File(this.cakeshop.getRoot().toString(), "cakeshop.py");
        FileUtils.write(file, "1\n2\n3\n4", "utf-8");
        file.createNewFile();

        String payload = String.format("{\"ID\": \"UserError\", \"VERSION\": \"%s\", \"path\": \"%s\", \"line\": 2}",
                UserError.VERSION, file);
        UserError event = (UserError) session.eventFactory(payload);
        assertThat(event).isInstanceOf(UserError.class);
        assertThat(event.getPath()).isEqualTo(file);
        assertThat(event.getLine()).isEqualTo(2);

        event.handle();
        verify(this.dialogFactoryFixture.dialogFactory, times(1)).showFirstUserErrorDialog(this.getProject());
        verify(handler.getErrorHighlightManager(), times(1)).add(
                eq(file), eq(2));
    }

    @Test
    public void testFrameError() throws Exception {
        PythonRunConfHandler handler = (PythonRunConfHandler) RunConfHandlerFactory.factory(cakeshop.getRunConf());
        HighlightManagerFixture highlightManagerFixture = new HighlightManagerFixture(handler);
        highlightManagerFixture.start();

        Session session = new Session(this.getProject(), handler);

        File file = new File(this.cakeshop.getRoot().toString(), "cakeshop.py");
        FileUtils.write(file, "1\n2\n3\n4", "utf-8");
        file.createNewFile();

        String payload = String.format("{\"ID\": \"FrameError\", \"VERSION\": \"%s\", " +
                        "\"fullname\": \"bake\", " +
                        "\"path\": \"%s\", \"line\": 2, " +
                        "\"frame_id\": 3423423442, \"body_lineno\": 3, \"handler_lineno\": 2}",
                FrameError.VERSION, file);
        FrameError event = (FrameError) session.eventFactory(payload);
        assertThat(event).isInstanceOf(FrameError.class);
        assertThat(event.getPath()).isEqualTo(file);
        assertThat(event.getFullname()).isEqualTo("bake");
        assertThat(event.getLine()).isEqualTo(2);
        assertThat(event.getFrameId()).isEqualTo(3423423442L);
        assertThat(event.getBodyLineno()).isEqualTo(3);
        assertThat(event.getHandlerLineno()).isEqualTo(2);

        event.handle();
        verify(this.dialogFactoryFixture.dialogFactory, times(1)).showFirstFrameErrorDialog(this.getProject());

        verify(handler.getErrorHighlightManager(), times(1)).add(
        eq(file), eq(2));
    }

    @Test
    public void testUpdateModule() throws Exception {
        PythonRunConfHandler handler = (PythonRunConfHandler) RunConfHandlerFactory.factory(cakeshop.getRunConf());
        HighlightManagerFixture highlightManagerFixture = new HighlightManagerFixture(handler);
        highlightManagerFixture.start();

        Session session = new Session(this.getProject(), handler);

        File file = new File(this.cakeshop.getRoot().toString(), "cakeshop.py");
        FileUtils.write(file, "1\n2\n3\n4", "utf-8");
        file.createNewFile();

        String payload = String.format("{\"ID\": \"UpdateModule\", \"VERSION\": \"%s\", \"path\": \"%s\", " +
                        "\"actions\": [{\"name\": \"Update\", \"obj\": \"Function\"," +
                        " \"line_start\": 2, \"line_end\": 4}]}",
                FrameError.VERSION, file.getAbsolutePath());

        UpdateModule event = (UpdateModule) session.eventFactory(payload);
        assertThat(event).isInstanceOf(UpdateModule.class);

        assertThat(event.actions.size()).isEqualTo(1);
        Action action = event.actions.get(0);
        assertThat(action.getLineStart()).isEqualTo(2);
        assertThat(action.getLineStart()).isEqualTo(2);
        assertThat(action.getLineEnd()).isEqualTo(4);
        assertThat(action.getName()).isEqualTo("Update");
        assertThat(action.getObj()).isEqualTo("Function");

        event.handle();

        verify(handler.getErrorHighlightManager(), times(1)).clearAll();
    }

    @Test
    public void testIncompatible() throws Exception {
        PythonRunConfHandler handler = (PythonRunConfHandler) RunConfHandlerFactory.factory(cakeshop.getRunConf());
        HighlightManagerFixture highlightManagerFixture = new HighlightManagerFixture(handler);
        highlightManagerFixture.start();

        Session session = new Session(this.getProject(), handler);

        File file = new File(this.cakeshop.getRoot().toString(), "cakeshop.py");
        FileUtils.write(file, "1\n2\n3\n4", "utf-8");
        file.createNewFile();

        String payload = "{\"ID\": \"UpdateModule\", \"VERSION\": \"0.0.0\"}";

        UpdateModule event = (UpdateModule) session.eventFactory(payload);
        assertThat(event).isNull();
    }

    @Test
    public void testUpdateFrame() throws IOException {
        PythonRunConfHandler handler = (PythonRunConfHandler) RunConfHandlerFactory.factory(cakeshop.getRunConf());
        Session session = new Session(this.getProject(), handler);

        File file = new File(this.cakeshop.getRoot().toString(), "cakeshop.py");
        FileUtils.write(file, "1\n2\n3\n4", "utf-8");
        file.createNewFile();

        String payload = String.format("{\"ID\": \"UpdateFrame\", \"VERSION\": \"%s\", \"path\": \"%s\", " +
                        "\"fullname\": \"bake\", " +
                        "\"frame_id\": 3423423442, \"body_lineno\": 3, \"handler_lineno\": 2}",
                UpdateFrame.VERSION, file.getAbsolutePath());

        UpdateFrame event = (UpdateFrame) session.eventFactory(payload);
        assertThat(event).isInstanceOf(UpdateFrame.class);
        assertThat(event.getPath()).isEqualTo(file);
        assertThat(event.getFullname()).isEqualTo("bake");
        assertThat(event.getFrameId()).isEqualTo(3423423442L);
        assertThat(event.getBodyLineno()).isEqualTo(3);
        assertThat(event.getHandlerLineno()).isEqualTo(2);

        event.handle();
    }
}