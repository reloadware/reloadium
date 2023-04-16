package rw.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyFromImportStatement;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyImportStatement;
import org.jetbrains.annotations.Nullable;
import rw.handler.RunConfHandler;
import rw.session.cmds.completion.GetCtxCompletion;
import rw.session.cmds.completion.GetGlobalCompletion;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.instanceOf;

public class GlobalCompletionContributor extends CtxCompletionContributor {
    static ElementPattern<? extends PsiElement> PATTERN = psiElement().andNot(psiElement().inside(PyFunction.class))
            .andNot(psiElement().inside(psiElement(PyFromImportStatement.class)))
            .andNot(psiElement().inside(psiElement(PyImportStatement.class)));

    protected static class Provider extends CtxCompletionContributor.Provider {
        @Nullable protected GetCtxCompletion cmdFactory(PsiElement element,
                                              String prompt,
                                              VirtualFile virtualFile,
                                              RunConfHandler handler,
                                              @Nullable String parent,
                                              CompletionMode mode) {
            String file = virtualFile.toNioPath().toFile().toString();
            file = handler.convertPathToRemote(file, false);
            return new GetGlobalCompletion(file, parent, prompt, mode);
        }
    }

    public GlobalCompletionContributor() {
        extend(CompletionType.BASIC,
                this.PATTERN,
                new Provider());
    }
}
