package rw.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.project.DumbAware;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;
import rw.session.cmds.completion.Suggestion;

import java.awt.*;
import java.util.List;

public class BaseCompletionContributor extends CompletionContributor implements DumbAware {
    static Color COMPLETION_COLOR = new JBColor(new Color(159, 97, 0), new Color(255, 156, 75));

    protected boolean shouldComplete(@NotNull CompletionParameters parameters,
                                     @NotNull CompletionResultSet result) {
        return true;
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        if (!this.shouldComplete(parameters, result)) {
            return;
        }

        super.fillCompletionVariants(parameters, result);
    }

    protected abstract static class Provider extends CompletionProvider<CompletionParameters> {
        protected void removeDuplicates(@NotNull List<Suggestion> suggestions, @NotNull CompletionParameters parameters,
                                        @NotNull CompletionResultSet result) {
            result.runRemainingContributors(parameters, r -> {
                if (suggestions.stream().noneMatch(s -> s.getName().equals(r.getLookupElement().getLookupString()))) {
                    result.passResult(r);
                }
            });
        }
    }
}