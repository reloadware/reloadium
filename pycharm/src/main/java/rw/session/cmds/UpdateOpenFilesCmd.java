package rw.session.cmds;

import java.util.List;

public class UpdateOpenFilesCmd extends Cmd {
    final String ID = "UpdateOpenFiles";

    private final List<String> files;

    public UpdateOpenFilesCmd(List<String> files) {
        this.files = files;
    }

    @Override
    public String getId() {
        return this.ID;
    }
}
