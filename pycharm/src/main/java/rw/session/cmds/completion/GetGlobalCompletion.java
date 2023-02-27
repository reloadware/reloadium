package rw.session.cmds.completion;

import org.jetbrains.annotations.Nullable;
import rw.completion.CompletionMode;

import java.io.File;

public class GetGlobalCompletion extends GetCtxCompletion {
    public final String ID = "GetGlobalCompletion";

    public GetGlobalCompletion(String file, @Nullable String parent, CompletionMode mode) {
        super(file, parent, mode);
    }

    @Override
    public String getId() {
        return this.ID;
    }
}
