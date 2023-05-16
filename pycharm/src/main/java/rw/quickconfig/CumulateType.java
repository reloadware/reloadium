package rw.quickconfig;

import com.google.gson.annotations.SerializedName;

public enum CumulateType {
    @SerializedName("Last")
    LAST("Last"),
    @SerializedName("Mean")
    MEAN("Mean"),
    @SerializedName("Add")
    ADD("Add"),
    @SerializedName("Min")
    MIN("Min"),
    @SerializedName("Max")
    MAX("Max");

    public static final CumulateType DEFAULT = ADD;

    public final String value;

    CumulateType(String value) {
        this.value = value;
    }

    static public CumulateType[] getAll() {
        return new CumulateType[]{CumulateType.LAST, CumulateType.MEAN, CumulateType.ADD, CumulateType.MAX, CumulateType.MIN};
    }

    public String toString() {
        return this.value;
    }
}