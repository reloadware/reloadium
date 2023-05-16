package rw.session.cmds;

import rw.quickconfig.QuickConfigState;

public class QuickConfigCmd extends Cmd {
    final String ID = "QuickConfig";
    QuickConfigState content;

    public QuickConfigCmd(QuickConfigState content) {
        this.content = content;
    }

    public String getId() {
        return this.ID;
    }
}
