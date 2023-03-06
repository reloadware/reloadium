package rw.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.ProcessingContext;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.frame.XStackFrame;
import com.jetbrains.python.psi.PySubscriptionExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.handler.BaseRunConfHandler;
import rw.handler.RunConfHandlerManager;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;
import rw.session.cmds.completion.GetCtxCompletion;
import rw.session.cmds.completion.Suggestion;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.intellij.codeInsight.completion.CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED;

public class CtxCompletionContributor extends BaseCompletionContributor {
    protected abstract static class Provider extends BaseCompletionContributor.Provider {
        public Provider() {
        }

        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                      @NotNull ProcessingContext context,
                                      @NotNull CompletionResultSet result) {
            PreferencesState preferences = Preferences.getInstance().getState();

            if (!preferences.runtimeCompletion) {
                return;
            }

            PsiElement element = parameters.getPosition();
            VirtualFile virtualFile = element.getContainingFile().getOriginalFile().getVirtualFile();
            PsiElement orig = parameters.getOriginalPosition();
            CompletionMode mode = this.getMode(orig);
            PsiElement prevElement = element.getPrevSibling();

            BaseRunConfHandler handler = RunConfHandlerManager.get().getCurrentDebugHandler(element.getProject());
            if (handler == null) {
                handler = RunConfHandlerManager.get().getCurrentRunHandler(element.getProject());
            }
            if (handler == null) {
                return;
            }

            String parent = null;

            if (mode == CompletionMode.KEY) {
                PySubscriptionExpression subscr = PsiTreeUtil.getParentOfType(element, PySubscriptionExpression.class);
                assert subscr != null;
                assert subscr.getQualifier() != null;
                parent = subscr.getQualifier().getText();

            } else {
                if (prevElement != null && prevElement.getText().equals(".")) {
                    parent = prevElement.getPrevSibling().getText();
                }
            }

            String prompt = element.getText().replace(DUMMY_IDENTIFIER_TRIMMED, "");

            GetCtxCompletion cmd = this.cmdFactory(element,
                    prompt,
                    virtualFile,
                    handler,
                    parent,
                    mode);

            if (cmd == null) {
                return;
            }

            GetCtxCompletion.Return completion = (GetCtxCompletion.Return) handler.getSession().send(cmd);

            List<Suggestion> suggestions = completion.getSuggestions();

            if (suggestions == null) {
                return;
            }

            Map<String, String> nameToTailText = new HashMap<>();

            result.runRemainingContributors(parameters, r -> {
                String lookupString = r.getLookupElement().getLookupString();
                if (!(lookupString.startsWith("__") && !prompt.startsWith("__"))) {
                    if (suggestions.stream().anyMatch(s -> s.getName().equals(lookupString))) {
                        LookupElementPresentation presentation = new LookupElementPresentation();
                        r.getLookupElement().renderElement(presentation);
                        if (presentation.getTailText() != null) {
                            nameToTailText.put(r.getLookupElement().getLookupString(), presentation.getTailText());
                        }
                    } else {
                        result.passResult(r);
                    }
                }
            });


            for (Suggestion s : suggestions) {
                Icon icon = CompletionUtils.TYPE_TO_ICON.getOrDefault(s.getPyType(), AllIcons.Nodes.Variable);

                String tailText;

                if (s.getTailText() != null) {
                    tailText = s.getTailText();
                } else {
                    tailText = nameToTailText.get(s.getName());
                }

                LookupElementBuilder builder = LookupElementBuilder.create(s.getName()).withItemTextForeground(COMPLETION_COLOR).withTypeText(s.getTypeText()).withTailText(tailText);

                if (mode == CompletionMode.KEY) {
                    builder = builder.withIcon(AllIcons.Nodes.Parameter);
                } else {
                    builder = builder.withIcon(icon);
                }

                result.addElement(builder);
            }
        }

        @Nullable
        abstract protected GetCtxCompletion cmdFactory(PsiElement element,
                                                       String prompt,
                                                       VirtualFile virtualFile,
                                                       BaseRunConfHandler handler,
                                                       @Nullable String parent,
                                                       CompletionMode mode);

        private CompletionMode getMode(PsiElement orig) {
            if (orig == null) {
                return CompletionMode.ATTRIBUTE;
            }

            if (orig.getParent().getParent() instanceof PySubscriptionExpression || orig.getParent() instanceof PySubscriptionExpression) {
                return CompletionMode.KEY;
            } else {
                return CompletionMode.ATTRIBUTE;
            }
        }
    }

    protected boolean shouldComplete(@NotNull CompletionParameters parameters,
                                     @NotNull CompletionResultSet result) {
        PsiElement element = parameters.getPosition();
        VirtualFile virtualFile = element.getContainingFile().getOriginalFile().getVirtualFile();
        boolean ret = !(virtualFile instanceof LightVirtualFile);
        return ret;
    }
}