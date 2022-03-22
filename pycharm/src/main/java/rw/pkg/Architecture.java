package rw.pkg;

public enum Architecture {
    x86("x86"),
    x64("x64"),
    x86_64("x86_64");

    public final String value;

    Architecture(String value) {
        this.value = value;
    }
}
