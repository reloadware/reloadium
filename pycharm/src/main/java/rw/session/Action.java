package rw.session;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import rw.handler.runConf.PythonRunConfHandler;

public class Action {
    private String name;
    private String obj;
    @SerializedName("line_start")
    private int lineStart;
    @SerializedName("line_end")
    private int lineEnd;

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
}
