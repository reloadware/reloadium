package rw.session.cmds.completion;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;
import rw.session.cmds.Cmd;

import java.util.List;

public class GetFromImportCompletion extends Cmd {
    public final String ID = "GetFromImportCompletion";
    @SerializedName("from_module")
    @Nullable String fromModule;
    String prompt;
    public GetFromImportCompletion(@Nullable String fromModule, String prompt) {
        this.prompt = prompt;
        this.fromModule = fromModule;
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
