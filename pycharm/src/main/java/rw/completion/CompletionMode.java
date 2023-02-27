package rw.completion;

public enum CompletionMode {
    ATTRIBUTE(0),
    KEY(1);

    final int value;

    CompletionMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
