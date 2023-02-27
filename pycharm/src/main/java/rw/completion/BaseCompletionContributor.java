package rw.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;
import rw.session.cmds.completion.Suggestion;

import java.util.List;

public class BaseCompletionContributor extends CompletionContributor implements DumbAware {
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