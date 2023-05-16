package rw.session.cmds.completion;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.completion.CompletionMode;
import rw.session.cmds.Cmd;

import java.util.List;

abstract public class GetCtxCompletion extends Cmd {
    String file;
    @Nullable String parent;
    int mode;
    String prompt;
    public GetCtxCompletion(String file, @Nullable String parent, String prompt, CompletionMode mode) {
        this.file = file;
        this.prompt = prompt;
        this.parent = parent;
        this.mode = mode.getValue();
    }

    public static class Return extends Cmd.Return {
        public List<Suggestion> suggestions;

        public Return(@NotNull List<Suggestion> suggestions) {
            this.suggestions = suggestions;
        }

        public List<Suggestion> getSuggestions() {
            return this.suggestions;
        }
    }
}
