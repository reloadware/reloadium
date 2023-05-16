package rw.session.events;

abstract public class FileError extends FileEvent {
    public static final String ID = "FileError";

    private Integer line;
    private String msg;

    @Override
    public void handle() {
        this.handler.getErrorHighlightManager().clearAll();
        this.handler.getErrorHighlightManager().add(this.getLocalPath(), this.line, this.msg);
    }

    public Integer getLine() {
        return line;
    }

    public String getMsg() {
        return msg;
    }
}
