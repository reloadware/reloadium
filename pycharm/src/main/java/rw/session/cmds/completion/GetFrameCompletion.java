package rw.session.cmds.completion;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;
import rw.completion.CompletionMode;

public class GetFrameCompletion extends GetCtxCompletion {
    public final String ID = "GetFrameCompletion";

    @SerializedName("thread_id")
    String threadId;

    @SerializedName("frame_id")
    String frameId;

    public GetFrameCompletion(String file, String threadId, String frameId, @Nullable String parent, String prompt, CompletionMode mode) {
        super(file, parent, prompt, mode);

        this.threadId = threadId;
        this.frameId = frameId;
    }

    @Override
    public String getId() {
        return this.ID;
    }
}
