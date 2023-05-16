package rw.quickconfig;

import com.google.gson.annotations.SerializedName;

public enum ProfilerType {
    @SerializedName("None")
    NONE("None"),
    @SerializedName("Time")
    TIME("Time"),
    @SerializedName("Memory")
    MEMORY("Memory");

    public static final ProfilerType DEFAULT = TIME;

    public final String value;

    ProfilerType(String value) {
        this.value = value;
    }

    static public ProfilerType[] getAll() {
        return new ProfilerType[]{ProfilerType.TIME, ProfilerType.MEMORY, ProfilerType.NONE};
    }

    public String toString() {
        return this.value;
    }
}