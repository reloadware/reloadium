package rw.tests.integr;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.testFramework.EdtTestUtil;
import com.jetbrains.python.PythonFileType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rw.handler.PythonRunConfHandler;
import rw.session.cmds.completion.GetCtxCompletion;
import rw.session.cmds.completion.Suggestion;
import rw.tests.BaseTestCase;
import rw.tests.fixtures.CakeshopFixture;
import rw.tests.fixtures.CurrentDebugHandlerFixture;
import rw.tests.fixtures.DebugPythonRunConfHandlerFixture;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class TestCompletion extends BaseTestCase {
    CurrentDebugHandlerFixture currentDebugHandlerFixture;
    CakeshopFixture cakeshop;
    PythonRunConfHandler handler;

    protected void assertSuggestionCount(int count) {
        LookupElement[] result = this.f.completeBasic();
        assertThat(result.length).isEqualTo(count);
    }

    protected void assertSuggestion(@NotNull Suggestion suggestion) {
        LookupElement[] result = this.f.completeBasic();

        for (LookupElement l : result) {
            if (l.getLookupString().equals(suggestion.getName())) {
                LookupElementPresentation presentation = new LookupElementPresentation();
                l.renderElement(presentation);
                if (presentation.getTailText() == null) {
                    continue;
                }

                if (presentation.getTypeText() == null) {
                    continue;
                }
                if (presentation.getTailText().equals(suggestion.getTailText()) && presentation.getTypeText().equals(suggestion.getTypeText())) {
                    return;
                }
            }
        }

        assert false;
    }

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        this.cakeshop = new CakeshopFixture(this.f);
        this.cakeshop.setUp();

        this.handler = new DebugPythonRunConfHandlerFixture(this.getProject(),
                this.cakeshop.getRunConf()).getHandler();

        this.currentDebugHandlerFixture = new CurrentDebugHandlerFixture(this.getProject(), handler);
        this.currentDebugHandlerFixture.setUp();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.cakeshop.tearDown();
        this.currentDebugHandlerFixture.tearDown();
        super.tearDown();
    }

    @Test
    public void testBasic() {
        String fileContent = """
                a = 1
                                
                """.stripIndent();

        this.f.configureByText(PythonFileType.INSTANCE, fileContent);
        Editor editor = this.f.getEditor();
        CaretModel caretModel = editor.getCaretModel();

        Suggestion suggestion = new Suggestion(
                "a", "int", "int", "Tail text"
        );

        GetCtxCompletion.Return cmdRet = new GetCtxCompletion.Return(List.of(suggestion));

        when(this.handler.getSession().send(any(), anyBoolean())).thenReturn(cmdRet);

        EdtTestUtil.runInEdtAndWait(() -> {
            caretModel.moveToOffset(fileContent.indexOf("1") + 1);
        });
        this.assertSuggestion(suggestion);
    }

    @Test
    public void testInsideString() {
        String fileContent = """
                a = 1
                msg = "test"
                """.stripIndent();

        this.f.configureByText(PythonFileType.INSTANCE, fileContent);
        Editor editor = this.f.getEditor();
        CaretModel caretModel = editor.getCaretModel();

        Suggestion suggestion = new Suggestion(
                "a", "int", "int", "Tail text"
        );

        GetCtxCompletion.Return cmdRet = new GetCtxCompletion.Return(List.of(suggestion));

        lenient().when(this.handler.getSession().send(any(), anyBoolean())).thenReturn(cmdRet);

        EdtTestUtil.runInEdtAndWait(() -> {
            caretModel.moveToOffset(fileContent.indexOf("\"") + 1);
        });

        assertThat(this.f.completeBasic()).isEmpty();
    }

    @Test
    public void testInsideFstring() {
        String fileContent = """
                a = 1
                msg = f"test"
                """.stripIndent();

        this.f.configureByText(PythonFileType.INSTANCE, fileContent);
        Editor editor = this.f.getEditor();
        CaretModel caretModel = editor.getCaretModel();

        Suggestion suggestion = new Suggestion(
                "a", "int", "int", "Tail text"
        );

        GetCtxCompletion.Return cmdRet = new GetCtxCompletion.Return(List.of(suggestion));

        lenient().when(this.handler.getSession().send(any(), anyBoolean())).thenReturn(cmdRet);

        EdtTestUtil.runInEdtAndWait(() -> {
            caretModel.moveToOffset(fileContent.indexOf("\"") + 1);
        });

        assertThat(this.f.completeBasic()).isEmpty();
    }

    @Test
    public void testInsideFstringBrackets() {
        String fileContent = """
                a = 1
                msg = f"test {}"
                """.stripIndent();

        this.f.configureByText(PythonFileType.INSTANCE, fileContent);
        Editor editor = this.f.getEditor();
        CaretModel caretModel = editor.getCaretModel();

        Suggestion suggestion = new Suggestion(
                "a", "int", "int", "Tail text"
        );

        GetCtxCompletion.Return cmdRet = new GetCtxCompletion.Return(List.of(suggestion));

        lenient().when(this.handler.getSession().send(any(), anyBoolean())).thenReturn(cmdRet);

        EdtTestUtil.runInEdtAndWait(() -> {
            caretModel.moveToOffset(fileContent.indexOf("{") + 1);
        });

        this.assertSuggestion(suggestion);
    }

    @Test
    public void testStrDictKey() {
        String fileContent = """
                numbers = {
                    "one": 1,
                    "two": 2
                }
                msg = numbers["
                """.stripIndent();

        this.f.configureByText(PythonFileType.INSTANCE, fileContent);
        Editor editor = this.f.getEditor();
        CaretModel caretModel = editor.getCaretModel();

        Suggestion suggestion1 = new Suggestion(
                "\"one\"", "int", "int", "Tail text1"
        );

        Suggestion suggestion2 = new Suggestion(
                "\"two\"", "int", "int", "Tail text2"
        );

        GetCtxCompletion.Return cmdRet = new GetCtxCompletion.Return(List.of(suggestion1, suggestion2));

        lenient().when(this.handler.getSession().send(any(), anyBoolean())).thenReturn(cmdRet);

        EdtTestUtil.runInEdtAndWait(() -> {
            caretModel.moveToOffset(fileContent.indexOf("[\"") + 2);
        });

        this.assertSuggestion(suggestion1);
    }

    @Test
    public void testStrDictKeyBeforeQuotes() {
        String fileContent = """
                numbers = {
                    "one": 1,
                    "two": 2
                }
                msg = numbers["
                """.stripIndent();

        this.f.configureByText(PythonFileType.INSTANCE, fileContent);
        Editor editor = this.f.getEditor();
        CaretModel caretModel = editor.getCaretModel();

        Suggestion suggestion1 = new Suggestion(
                "\"one\"", "int", "int", "Tail text1"
        );

        Suggestion suggestion2 = new Suggestion(
                "\"two\"", "int", "int", "Tail text2"
        );

        GetCtxCompletion.Return cmdRet = new GetCtxCompletion.Return(List.of(suggestion1, suggestion2));

        lenient().when(this.handler.getSession().send(any(), anyBoolean())).thenReturn(cmdRet);

        EdtTestUtil.runInEdtAndWait(() -> {
            caretModel.moveToOffset(fileContent.indexOf("[\"") + 1);
        });

        this.assertSuggestion(suggestion1);
    }
}
