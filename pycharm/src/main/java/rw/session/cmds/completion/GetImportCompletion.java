package rw.session.cmds.completion;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;
import rw.completion.CompletionMode;
import rw.session.cmds.Cmd;

import java.util.List;

public class GetImportCompletion extends Cmd {
    public final String ID = "GetImportCompletion";

    public class Return extends Cmd.Return {
        List<Suggestion> suggestions;

        public List<Suggestion> getSuggestions() {
            return this.suggestions;
        }
    }

    @Nullable String parent;

    public GetImportCompletion(@Nullable String parent) {
        this.parent = parent;
    }

    @Override
    public String getId() {
        return this.ID;
    }
}
