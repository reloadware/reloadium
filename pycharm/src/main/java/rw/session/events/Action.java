package rw.session.events;

import com.google.gson.annotations.SerializedName;

public class Action {
    private String name;
    private String obj;
    @SerializedName("line_start")
    private int lineStart;
    @SerializedName("line_end")
    private int lineEnd;
    private boolean blink;

    public int getLineEnd() {
        return lineEnd;
    }

    public int getLineStart() {
        return lineStart;
    }

    public String getName() {
        return name;
    }

    public String getObj() {
        return obj;
    }

    public boolean shouldBlink() {
        return blink;
    }
}
