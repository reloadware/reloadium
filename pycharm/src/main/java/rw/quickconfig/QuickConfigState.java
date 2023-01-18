package rw.quickconfig;

import com.google.gson.annotations.SerializedName;

public class QuickConfigState {
    private ProfilerType profiler;
    @SerializedName("frame_scope")
    private Boolean frameScope;
    @SerializedName("cumulate_type")
    private CumulateType cumulateType;
    @SerializedName("error_handling_mode")
    private ErrorHandlingMode errorHandlingMode;

    QuickConfigState(ProfilerType profiler, Boolean frameScope, CumulateType cumulateType, ErrorHandlingMode errorHandlingMode) {
        this.profiler = profiler;
        this.frameScope = frameScope;
        this.cumulateType = cumulateType;
        this.errorHandlingMode = errorHandlingMode;
    }

    public ProfilerType getProfiler() {
        return this.profiler;
    }
    public Boolean getFrameScope() {
        return this.frameScope;
    }
    public CumulateType getComulateType() {
        return this.cumulateType;
    }

    public ErrorHandlingMode getErrorHandlingMode() {
        return this.errorHandlingMode;
    }
}
