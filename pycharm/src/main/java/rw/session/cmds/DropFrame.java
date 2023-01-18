package rw.session.cmds;

public class DropFrame extends FrameCmd {
    final String ID = "DropFrame";

    public DropFrame(String frameId) {
        super(frameId);
    }

    @Override
    public String getId() {
        return this.ID;
    }
}
