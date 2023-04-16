package rw.session.cmds.completion;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;
import rw.completion.CompletionMode;

public class GetFrameCompletion extends GetCtxCompletion {
    public final String ID = "GetFrameCompletion";

    @SerializedName("frame_id")
    String frameId;

    public GetFrameCompletion(String file, String frameId, @Nullable String parent, String prompt, CompletionMode mode) {
        super(file, parent, prompt, mode);

        this.frameId = frameId;
    }

    @Override
    public String getId() {
        return this.ID;
    }
}
