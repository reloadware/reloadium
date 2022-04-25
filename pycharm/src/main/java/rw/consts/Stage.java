package rw.consts;

public enum Stage {
    LOCAL("local"),
    CI("ci"),
    STAGE("stage"),
    PROD("prod");

    public final String value;

    Stage(String value) {
        this.value = value;
    }
}
