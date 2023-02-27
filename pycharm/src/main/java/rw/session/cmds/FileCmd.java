package rw.session.cmds;

abstract public class FileCmd extends Cmd {
    private String file;

    public FileCmd(String file) {
        this.file = file;
    }
}
