package rw.highlights;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.codeInsight.hint.HintUtil.ERROR_COLOR_KEY;
import static com.intellij.openapi.editor.colors.EditorColorsUtil.getGlobalOrDefaultColor;

public class ErrorHighlighter {
    Highlighter highlighter;
    File file;
    int line;
    String msg;
    Project project;
    List<Inlay<ErrorRenderer>> inlays;

    public ErrorHighlighter(Project project, File file, int line, String msg) {
        this.file = file;
        this.msg = msg;
        this.project = project;
        this.line = line;

        this.inlays = new ArrayList<>();

        this.highlighter = new Highlighter(this.project, file, line, getGlobalOrDefaultColor(ERROR_COLOR_KEY), 0, false);
    }

    public void show() {
        this.highlighter.show();

        VirtualFile file = new VirtualFileWrapper(this.file).getVirtualFile(false);
        assert file != null;
        Document document = ReadAction.compute(() -> FileDocumentManager.getInstance().getDocument(file));

        for (Editor e : EditorFactory.getInstance().getEditors(document)) {
            InlayModel inlayModel = e.getInlayModel();

            int line = this.line-1;

            ApplicationManager.getApplication().invokeLater(() -> {
                ErrorRenderer renderer = new ErrorRenderer(e, this.msg);
                e.getCaretModel().moveToLogicalPosition(new LogicalPosition(line, 0));
                e.getScrollingModel().scrollToCaret(ScrollType.CENTER);
                int offset = e.logicalPositionToOffset(new LogicalPosition(line, 0));
                Inlay<ErrorRenderer> inlay = inlayModel.addBlockElement(offset, true, false, 100, renderer);
                this.inlays.add(inlay);
            });
        }
    }

    public void hide() {
        this.highlighter.hide();

        ApplicationManager.getApplication().invokeLater(() -> {
            for (Inlay<ErrorRenderer> i : this.inlays) {
                i.dispose();
            }
        });
    }
}
