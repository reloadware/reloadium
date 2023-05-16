package rw.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.frame.XSuspendContext;
import com.jetbrains.python.debugger.PyExecutionStack;
import com.jetbrains.python.debugger.PyStackFrame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.handler.RunConfHandler;
import rw.session.cmds.completion.GetCtxCompletion;
import rw.session.cmds.completion.GetFrameCompletion;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class FrameCompletionContributor extends CtxCompletionContributor {
    public FrameCompletionContributor() {
        extend(CompletionType.BASIC,
                psiElement(),
                new Provider());
    }

    protected boolean shouldComplete(@NotNull CompletionParameters parameters,
                                     @NotNull CompletionResultSet result) {
        PsiElement element = parameters.getPosition();
        VirtualFile virtualFile = element.getContainingFile().getOriginalFile().getVirtualFile();
        boolean ret = virtualFile instanceof LightVirtualFile;
        return ret;
    }

    protected static class Provider extends CtxCompletionContributor.Provider {
        @Nullable
        protected GetCtxCompletion cmdFactory(PsiElement element,
                                              String prompt,
                                              VirtualFile virtualFile,
                                              RunConfHandler handler,
                                              @Nullable String parent,
                                              CompletionMode mode) {
            assert handler.getDebugSession() != null;

            XSuspendContext suspendContext = handler.getDebugSession().getSuspendContext();
            if (suspendContext == null) {
                return null;
            }

            PyExecutionStack executionStack = (PyExecutionStack) suspendContext.getActiveExecutionStack();
            assert executionStack != null;

            PyStackFrame stackFrame = (PyStackFrame) handler.getDebugSession().getCurrentStackFrame();
            if (stackFrame == null) {
                return null;
            }
            XSourcePosition sourcePosition = stackFrame.getSourcePosition();

            if (sourcePosition == null) {
                return null;
            }
            String file = sourcePosition.getFile().toNioPath().toFile().toString();
            String frameId = stackFrame.getFrameId();
            file = handler.convertPathToRemote(file, false);

            return new GetFrameCompletion(file, frameId, parent, prompt, mode);
        }
    }
}
