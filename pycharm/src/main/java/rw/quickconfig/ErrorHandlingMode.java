package rw.quickconfig;

import com.google.gson.annotations.SerializedName;

public enum ErrorHandlingMode {
    @SerializedName("BreakpointPresent")
    BREAKPOINT_PRESENT("Breakpoint Present"),
    @SerializedName("Always")
    ALWAYS("Always");

    public static final ErrorHandlingMode DEFAULT = ALWAYS;

    public final String value;

    ErrorHandlingMode(String value) {
        this.value = value;
    }

    static public ErrorHandlingMode[] getAll() {
        return new ErrorHandlingMode[]{ErrorHandlingMode.ALWAYS, ErrorHandlingMode.BREAKPOINT_PRESENT};
    }

    public String toString() {
        return this.value;
    }
}