package rw.session.cmds.completion;

import org.jetbrains.annotations.Nullable;
import rw.session.cmds.Cmd;

import java.util.List;

public class GetImportCompletion extends Cmd {
    public final String ID = "GetImportCompletion";
    @Nullable String parent;
    String prompt;
    public GetImportCompletion(@Nullable String parent, String prompt) {
        this.parent = parent;
        this.prompt = prompt;
    }

    @Override
    public String getId() {
        return this.ID;
    }

    public class Return extends Cmd.Return {
        List<Suggestion> suggestions;

        public List<Suggestion> getSuggestions() {
            return this.suggestions;
        }
    }
}
