package rw.session;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import rw.dialogs.DialogFactory;
import rw.frame.Frame;
import rw.handler.runConf.BaseRunConfHandler;
import rw.handler.runConf.PythonRunConfHandler;

import java.io.File;

abstract public class FrameEvent extends FileEvent {
    @SerializedName("frame_id")
    private Long frameId;
    @SerializedName("body_lineno")
    private Integer bodyLineno;

    @SerializedName("handler_lineno")
    private Integer handlerLineno;

    private String fullname;

    public Long getFrameId() {
        return frameId;
    }

    @Override
    public void handle() {
        super.handle();
    }

    public Integer getBodyLineno() {
        return this.bodyLineno;
    }

    public Integer getHandlerLineno() {
        return this.handlerLineno;
    }

    public String getFullname() {
        return this.fullname;
    }
}
