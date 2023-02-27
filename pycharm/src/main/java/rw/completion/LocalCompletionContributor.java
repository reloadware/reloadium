package rw.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.PyFunction;
import org.jetbrains.annotations.Nullable;
import rw.handler.BaseRunConfHandler;
import rw.session.cmds.completion.GetCtxCompletion;
import rw.session.cmds.completion.GetLocalCompletion;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class LocalCompletionContributor extends CtxCompletionContributor {
    protected static class Provider extends CtxCompletionContributor.Provider {
        protected GetCtxCompletion cmdFactory(PsiElement element,
                                              BaseRunConfHandler handler,
                                              @Nullable String parent,
                                              CompletionMode mode) {
            VirtualFile virtualFile = element.getContainingFile().getOriginalFile().getVirtualFile();
            Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
            String file = virtualFile.toNioPath().toFile().toString();
            file = handler.convertPathToRemote(file, false);
            String threadId = handler.getThreadErrorManager().getActiveThread();

            PyFunction function = PsiTreeUtil.getParentOfType(element, PyFunction.class);
            
            if(function == null) {
                return null;
            }

            assert document != null;

            int promptLine = document.getLineNumber(element.getTextOffset()) + 1;
            return new GetLocalCompletion(file, threadId, function.getName(), promptLine, parent, mode);
        }
    }

    public LocalCompletionContributor() {
        extend(CompletionType.BASIC,
                psiElement().inside(false, psiElement(PyFunction.class)),
                new Provider());
    }
}
