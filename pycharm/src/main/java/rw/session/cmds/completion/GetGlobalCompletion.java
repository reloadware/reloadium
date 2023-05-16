package rw.session.cmds.completion;

import org.jetbrains.annotations.Nullable;
import rw.completion.CompletionMode;

public class GetGlobalCompletion extends GetCtxCompletion {
    public final String ID = "GetGlobalCompletion";

    public GetGlobalCompletion(String file, @Nullable String parent, String prompt, CompletionMode mode) {
        super(file, parent, prompt, mode);
    }

    @Override
    public String getId() {
        return this.ID;
    }
}
