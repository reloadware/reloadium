package rw.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.python.PyNames;
import com.jetbrains.python.psi.PyFromImportStatement;
import icons.PythonPsiApiIcons;
import org.jetbrains.annotations.NotNull;
import rw.consts.Const;
import rw.handler.BaseRunConfHandler;
import rw.handler.RunConfHandlerManager;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;
import rw.session.cmds.completion.GetFromImportCompletion;
import rw.session.cmds.completion.Suggestion;

import javax.swing.*;
import java.util.List;

import static com.intellij.codeInsight.completion.CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED;
import static com.intellij.patterns.PlatformPatterns.psiElement;

public class FromImportCompletionContributor extends BaseCompletionContributor {
    static ElementPattern<? extends PsiElement> PATTERN = psiElement().afterLeaf(psiElement().withText(PyNames.IMPORT)).and(psiElement().inside(PyFromImportStatement.class));

    private static final class Provider extends BaseCompletionContributor.Provider {
        private Provider() {
        }

        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                      @NotNull ProcessingContext context,
                                      @NotNull CompletionResultSet result) {
            PreferencesState preferences = Preferences.getInstance().getState();
            if(!preferences.runtimeCompletion) {
                return;
            }

            PyFromImportStatement element = PsiTreeUtil.getParentOfType(parameters.getPosition(), PyFromImportStatement.class);

            if (element == null) {
                return;
            }

            BaseRunConfHandler handler = RunConfHandlerManager.get().getCurrentHandler(parameters.getPosition().getProject());
            if (handler == null) {
                return;
            }

            assert element.getImportSource() != null;

            String fromModule = element.getImportSource().getText().replace(DUMMY_IDENTIFIER_TRIMMED, "");

            GetFromImportCompletion cmd = new GetFromImportCompletion(fromModule);
            GetFromImportCompletion.Return completion = (GetFromImportCompletion.Return) handler.getSession().send(cmd);

            List<Suggestion> suggestions = completion.getSuggestions();
            if (suggestions == null) {
                return;
            }

            this.removeDuplicates(suggestions, parameters, result);

            for (Suggestion s : suggestions) {
                Icon icon = CompletionUtils.TYPE_TO_ICON.getOrDefault(s.getPyType(), PythonPsiApiIcons.PythonFile);

                String name = s.getName();
                LookupElementBuilder builder = LookupElementBuilder.create(name).withItemTextForeground(Const.get().brandColor).withIcon(icon).withTailText(s.getTailText()).withTypeText(s.getTypeText());
                result.addElement(builder);
            }
        }
    }

    public FromImportCompletionContributor() {
        extend(CompletionType.BASIC,
                this.PATTERN,
                new Provider());
    }
}