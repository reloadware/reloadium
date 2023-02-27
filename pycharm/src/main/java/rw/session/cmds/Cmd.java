package rw.session.cmds;

import org.jetbrains.annotations.Nullable;

abstract public class Cmd {
    public static class Return {
    }

    public Cmd() {
    }

    public abstract String getId();
}
