package rw.session.cmds.completion;

import com.google.gson.annotations.SerializedName;

public class Suggestion {
    String name;

    @SerializedName("py_type")
    String pyType;

    @SerializedName("type_text")
    String typeText;

    @SerializedName("tail_text")
    String tailText;

    public String getName() {
        return this.name;
    }

    public String getTypeText() {
        return this.typeText;
    }

    public String getTailText() {
        return this.tailText;
    }

    public String getPyType() {
        return this.pyType;
    }
}