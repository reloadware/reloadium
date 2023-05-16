package rw.session.cmds;

public class ReloadFile extends FileCmd {
    final String ID = "ReloadFile";

    private String content;

    public ReloadFile(String file, String content) {
        super(file);
        this.content = content;
    }

    public String getId() {
        return this.ID;
    }
}
