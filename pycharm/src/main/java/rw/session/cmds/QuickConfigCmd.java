package rw.session.cmds;

import rw.quickconfig.QuickConfigState;

public class QuickConfigCmd extends Cmd {
    final String ID = "QuickConfig";
    final String VERSION = "0.1.0";
    QuickConfigState content;

    public QuickConfigCmd(QuickConfigState content) {
        this.content = content;
    }
    public String getId() {
        return this.ID;
    }
}
