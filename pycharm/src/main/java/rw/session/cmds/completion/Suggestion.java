package rw.session.cmds.completion;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public class Suggestion {
    String name;

    @SerializedName("py_type")
    String pyType;

    @SerializedName("type_text")
    String typeText;

    @SerializedName("tail_text")
    String tailText;

    public Suggestion(@NotNull String name,
                      @NotNull String pyType,
                      @NotNull String typeText,
                      @NotNull String tailText) {
        this.name = name;
        this.pyType = pyType;
        this.typeText = typeText;
        this.tailText = tailText;
    }

    Suggestion() {

    }

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