package rw.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.python.PyNames;
import com.jetbrains.python.psi.PyFromImportStatement;
import com.jetbrains.python.psi.PyImportStatement;
import com.jetbrains.python.psi.PyImportStatementBase;
import org.jetbrains.annotations.NotNull;
import rw.handler.RunConfHandler;
import rw.handler.RunConfHandlerManager;
import rw.icons.Icons;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;
import rw.session.cmds.Cmd;
import rw.session.cmds.completion.GetImportCompletion;
import rw.session.cmds.completion.Suggestion;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

import static com.intellij.codeInsight.completion.CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED;
import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.jetbrains.python.codeInsight.completion.PyKeywordCompletionContributor.IN_FROM_IMPORT_AFTER_REF;

public class ImportCompletionContributor extends BaseCompletionContributor {
    static ElementPattern<? extends PsiElement> PATTERN = PlatformPatterns.psiElement().andOr(
            psiElement().inside(PyImportStatement.class).andNot(psiElement().inside(PyFromImportStatement.class)),
            psiElement().inside(PyFromImportStatement.class)
                    .andNot(psiElement().afterLeaf(psiElement().withText(PyNames.IMPORT)))
                    .andNot(IN_FROM_IMPORT_AFTER_REF));

    public ImportCompletionContributor() {
        extend(CompletionType.BASIC,
                this.PATTERN,
                new Provider());
    }

    private static final class Provider extends BaseCompletionContributor.Provider {
        private Provider() {
        }

        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                      @NotNull ProcessingContext context,
                                      @NotNull CompletionResultSet result) {
            PreferencesState preferences = Preferences.get().getState();
            if (!preferences.runtimeCompletion) {
                return;
            }

            PyImportStatementBase element = PsiTreeUtil.getParentOfType(parameters.getPosition(),
                    PyImportStatement.class,
                    PyFromImportStatement.class);

            if (element == null) {
                return;
            }

            RunConfHandler handler = RunConfHandlerManager.get().getCurrentDebugHandler(parameters.getPosition().getProject());
            if (handler == null) {
                return;
            }

            PsiElement lastChild = element.getLastChild();

            if (lastChild == null) {
                return;
            }

            String parent;
            if (element instanceof PyFromImportStatement) {
                parent = ((PyFromImportStatement) element).getImportSource().getText().replace(DUMMY_IDENTIFIER_TRIMMED, "");
            } else if (element instanceof PyImportStatement) {
                parent = element.getImportElements()[0].getText().replace(DUMMY_IDENTIFIER_TRIMMED, "");
            } else {
                return;
            }

            if (parent.endsWith(".")) {
                parent = parent.substring(0, parent.length() - 1);
            } else if (parent.contains(".")) {
                String[] parts = parent.split("\\.");
                parts = Arrays.copyOfRange(parts, 0, parts.length - 1);
                parent = String.join(".", parts);
            } else {
                parent = null;
            }

            String prompt = element.getText().replace(DUMMY_IDENTIFIER_TRIMMED, "");

            GetImportCompletion cmd = new GetImportCompletion(parent, prompt);
            Cmd.Return completion = handler.getSession().send(cmd, true);

            if (completion == null) {
                return;
            }

            List<Suggestion> suggestions = ((GetImportCompletion.Return) completion).getSuggestions();
            if (suggestions == null) {
                return;
            }

            this.removeDuplicates(suggestions, parameters, result);

            for (Suggestion s : suggestions) {
                Icon icon = CompletionUtils.TYPE_TO_ICON.getOrDefault(s.getPyType(), Icons.PythonFile);

                String name = s.getName();
                LookupElementBuilder builder = LookupElementBuilder.create(name).withItemTextForeground(COMPLETION_COLOR).withIcon(icon);

                result.addElement(builder);
            }
        }
    }
}