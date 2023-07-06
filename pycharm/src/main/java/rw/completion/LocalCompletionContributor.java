package rw.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.PyFunction;
import org.jetbrains.annotations.Nullable;
import rw.action.RunType;
import rw.handler.RunConfHandler;
import rw.session.cmds.completion.GetCtxCompletion;
import rw.session.cmds.completion.GetLocalCompletion;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class LocalCompletionContributor extends CtxCompletionContributor {
    public LocalCompletionContributor() {
        extend(CompletionType.BASIC,
                psiElement().inside(false, psiElement(PyFunction.class)),
                new Provider());
    }

    protected static class Provider extends CtxCompletionContributor.Provider {
        @Nullable
        protected GetCtxCompletion cmdFactory(PsiElement element,
                                              String prompt,
                                              VirtualFile virtualFile,
                                              RunConfHandler handler,
                                              @Nullable String parent,
                                              CompletionMode mode) {
            Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
            String file = virtualFile.toNioPath().toString();
            file = handler.convertPathToRemote(file, false);

            String threadId;
            if (handler.getRunType() == RunType.DEBUG) {
                threadId = handler.getThreadErrorManager().getActiveThread();
            } else {
                threadId = null;
            }

            PyFunction function = PsiTreeUtil.getParentOfType(element, PyFunction.class);

            if (function == null) {
                return null;
            }

            assert document != null;

            int promptLine = document.getLineNumber(element.getTextOffset()) + 1;
            return new GetLocalCompletion(file, threadId, function.getName(), promptLine, parent, prompt, mode);
        }
    }
}
