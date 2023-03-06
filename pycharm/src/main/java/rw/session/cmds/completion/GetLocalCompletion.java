package rw.session.cmds.completion;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;
import rw.completion.CompletionMode;

public class GetLocalCompletion extends GetCtxCompletion {
    public final String ID = "GetLocalCompletion";

    @SerializedName("thread_id")
    String threadId;

    @SerializedName("frame_name")
    String frameName;

    @SerializedName("prompt_line")
    int promptLine;

    public GetLocalCompletion(String file, String threadId, String frameName, int promptLine, @Nullable String parent, String prompt, CompletionMode mode) {
        super(file, parent, prompt, mode);
        this.threadId = threadId;
        this.frameName = frameName;
        this.promptLine = promptLine;
    }

    @Override
    public String getId() {
        return this.ID;
    }
}