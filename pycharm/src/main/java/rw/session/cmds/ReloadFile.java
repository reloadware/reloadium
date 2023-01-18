package rw.session.cmds;

public class ReloadFile extends FileCmd {
    final String ID = "ReloadFile";

    public ReloadFile(String path) {
        super(path);
    }
    public String getId() {
        return this.ID;
    }
}
