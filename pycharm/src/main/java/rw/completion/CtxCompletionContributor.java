package rw.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.ProcessingContext;
import com.jetbrains.python.PyTokenTypes;
import com.jetbrains.python.psi.PyFormattedStringElement;
import com.jetbrains.python.psi.PyPlainStringElement;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import com.jetbrains.python.psi.PySubscriptionExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.handler.RunConfHandler;
import rw.handler.RunConfHandlerManager;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;
import rw.session.cmds.Cmd;
import rw.session.cmds.completion.GetCtxCompletion;
import rw.session.cmds.completion.Suggestion;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.intellij.codeInsight.completion.CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED;

public class CtxCompletionContributor extends BaseCompletionContributor {
    static protected boolean isDictKey(@NotNull PsiElement element) {
        boolean ret = (element instanceof PyPlainStringElement) && (element.getParent().getParent() instanceof PySubscriptionExpression);
        ret |= element.getParent() instanceof PySubscriptionExpression;
        return ret;
    }

    private boolean isString(PsiElement element) {
        if (element instanceof PyPlainStringElement) {
            return true;
        }

        if (element.getParent() instanceof PyFormattedStringElement) {
            return true;
        }

        return false;
    }

    protected boolean shouldComplete(@NotNull CompletionParameters parameters,
                                     @NotNull CompletionResultSet result) {
        PsiElement element = parameters.getPosition();
        VirtualFile virtualFile = element.getContainingFile().getOriginalFile().getVirtualFile();
        boolean ret = !(virtualFile instanceof LightVirtualFile);
        ret &= !this.isString(element);
        ret |= this.isDictKey(element);
        return ret;
    }

    protected abstract static class Provider extends BaseCompletionContributor.Provider {
        public Provider() {
        }

        private static String findPrefix(final PsiElement element, final int offset) {
            PyStringLiteralExpression prevElement = PsiTreeUtil.getParentOfType(element, PyStringLiteralExpression.class);
            if (prevElement == null) {
                return "";
            }
            String ret = TextRange.create(prevElement.getTextRange().getStartOffset(), offset).substring(element.getContainingFile().getText());
            ;
            return ret;
        }

        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                      @NotNull ProcessingContext context,
                                      @NotNull CompletionResultSet result) {
            PreferencesState preferences = Preferences.get().getState();

            if (!preferences.runtimeCompletion) {
                return;
            }

            PsiElement element = parameters.getPosition();
            VirtualFile virtualFile = element.getContainingFile().getOriginalFile().getVirtualFile();
            PsiElement orig = parameters.getOriginalPosition();
            CompletionMode mode = this.getMode(orig);
            PsiElement prevElement = element.getPrevSibling();

            RunConfHandler handler = RunConfHandlerManager.get().getCurrentDebugHandler(element.getProject());
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

            Cmd.Return completion = handler.getSession().send(cmd, true);

            if (!(completion instanceof GetCtxCompletion.Return)) {
                return;
            }

            List<Suggestion> suggestions = ((GetCtxCompletion.Return) completion).getSuggestions();

            if (suggestions == null) {
                return;
            }

            Map<String, String> nameToTailText = new HashMap<>();


            for (Suggestion s : suggestions) {
                ObjectType objectType = ObjectType.nameToType.getOrDefault(s.getPyType(), ObjectType.Any);

                Icon icon = CompletionUtils.TYPE_TO_ICON.getOrDefault(objectType, AllIcons.Nodes.Variable);

                String tailText;

                if (s.getTailText() != null) {
                    tailText = s.getTailText();
                } else {
                    tailText = nameToTailText.get(s.getName());
                }

                LookupElementBuilder builder = LookupElementBuilder.create(s.getName()).withItemTextForeground(COMPLETION_COLOR).withTypeText(s.getTypeText()).withTailText(tailText);

                if (ObjectType.callableTypes.contains(objectType)) {
                    builder = builder.withInsertHandler((ctx, item) -> {
                        ctx.getDocument().insertString(ctx.getSelectionEndOffset(), "()");
                        Editor editor = ctx.getEditor();
                        editor.getCaretModel().moveToOffset(ctx.getSelectionEndOffset() - 1);
                    });
                }

                if (mode == CompletionMode.KEY) {
                    builder = builder.withIcon(AllIcons.Nodes.Parameter);
                    builder = builder.withInsertHandler(new InsertHandler<>() {
                        @Override
                        public void handleInsert(@NotNull final InsertionContext context, @NotNull final LookupElement item) {
                            final PyStringLiteralExpression str = PsiTreeUtil.findElementOfClassAtOffset(context.getFile(), context.getStartOffset(),
                                    PyStringLiteralExpression.class, false);
                            if (str != null) {
                                final boolean isDictKeys = PsiTreeUtil.getParentOfType(str, PySubscriptionExpression.class) != null;
                                if (isDictKeys) {
                                    final int off = context.getStartOffset() + str.getTextLength();
                                    final PsiElement element = context.getFile().findElementAt(off);
                                    final boolean atRBrace = element == null || element.getNode().getElementType() == PyTokenTypes.RBRACKET;
                                    final boolean badQuoting =
                                            (!StringUtil.startsWithChar(str.getText(), '\'') || !StringUtil.endsWithChar(str.getText(), '\'')) &&
                                                    (!StringUtil.startsWithChar(str.getText(), '"') || !StringUtil.endsWithChar(str.getText(), '"'));
                                    if (badQuoting || !atRBrace) {
                                        final Document document = context.getEditor().getDocument();
                                        final int offset = context.getTailOffset();
                                        document.deleteString(offset - 1, offset);
                                    }
                                }
                            }
                        }
                    });
                    result.withPrefixMatcher(findPrefix(orig, parameters.getOffset())).addElement(PrioritizedLookupElement.withPriority(builder, 1000000));
                } else {
                    builder = builder.withIcon(icon);
                    result.addElement(builder);
                }
            }

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
        }

        @Nullable
        abstract protected GetCtxCompletion cmdFactory(PsiElement element,
                                                       String prompt,
                                                       VirtualFile virtualFile,
                                                       RunConfHandler handler,
                                                       @Nullable String parent,
                                                       CompletionMode mode);

        private CompletionMode getMode(PsiElement orig) {
            if (orig == null) {
                return CompletionMode.ATTRIBUTE;
            }

            if (isDictKey(orig)) {
                return CompletionMode.KEY;
            } else {
                return CompletionMode.ATTRIBUTE;
            }
        }
    }
}