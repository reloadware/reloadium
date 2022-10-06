package rw.session.cmds;

public class ReloadFile extends FileCmd {
    final String ID = "ReloadFile";
    final String VERSION = "0.1.0";

    public ReloadFile(String path) {
        super(path);
    }
    public String getId() {
        return this.ID;
    }
}
