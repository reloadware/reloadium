package rw.session;

abstract public class FileError extends FileEvent {
    public static final String ID = "FileError";
    public static final String VERSION = "0.1.0";

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
