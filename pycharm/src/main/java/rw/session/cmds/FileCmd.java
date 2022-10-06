package rw.session.cmds;

abstract public class FileCmd extends Cmd {
    private String path;

    public FileCmd(String path) {
        this.path = path;
    }
}
