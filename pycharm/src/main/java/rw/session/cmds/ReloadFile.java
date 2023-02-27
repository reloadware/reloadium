package rw.session.cmds;

public class ReloadFile extends FileCmd {
    final String ID = "ReloadFile";

    public ReloadFile(String file) {
        super(file);
    }
    public String getId() {
        return this.ID;
    }
}
