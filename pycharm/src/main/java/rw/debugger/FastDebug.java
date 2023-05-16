package rw.debugger;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SyntaxTraverser;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.PyFunction;
import rw.handler.Activable;
import rw.highlights.Highlighter;
import rw.session.events.FunctionTraced;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FastDebug implements Activable {
    List<Highlighter> highlighters;
    Project project;

    Color TRACED_COLOR = new Color(0, 178, 44, 15);

    public FastDebug(Project project) {
        this.highlighters = new ArrayList<>();
        this.project = project;
    }

    public void onFunctionTracedEvent(FunctionTraced event) {
        PsiManager psiManager = PsiManager.getInstance(this.project);

        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        VirtualFile virtualFile = new VirtualFileWrapper(event.getLocalPath()).getVirtualFile(false);
        if (virtualFile == null) {
            return;
        }

        Document document = fileDocumentManager.getDocument(virtualFile);
        assert document != null;

        PyFile psiFile = (PyFile) psiManager.findFile(virtualFile);
        assert psiFile != null;

        PyFunction pyFunction = null;
        int startLine = 0;
        int endLine = 0;

        for (PyFunction e : SyntaxTraverser.psiTraverser(psiFile).filter(PyFunction.class).
                filter(e -> event.getName().equals(e.getName()))) {

            PsiElement identifier = e.getNameIdentifier();
            if (identifier == null) {
                continue;
            }

            startLine = document.getLineNumber(identifier.getTextRange().getStartOffset()) + 1;
            endLine = document.getLineNumber(e.getTextRange().getEndOffset()) + 1;

            if (event.getLine() >= startLine && event.getLine() <= endLine) {
                pyFunction = e;
                break;
            }
        }

        if (pyFunction == null) {
            return;
        }

        Highlighter highlighter = new Highlighter(this.project,
                event.getLocalPath(),
                startLine,
                endLine,
                this.TRACED_COLOR,
                -10000,
                false);

        highlighter.show();
        this.highlighters.add(highlighter);
    }

    @Override
    public void activate() {
        this.highlighters.forEach(Highlighter::show);
    }

    @Override
    public void deactivate() {
        this.highlighters.forEach(Highlighter::hide);
    }
}
