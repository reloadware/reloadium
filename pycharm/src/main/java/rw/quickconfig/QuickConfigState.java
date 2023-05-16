package rw.quickconfig;

import com.google.gson.annotations.SerializedName;

public class QuickConfigState {
    private ProfilerType profiler;
    @SerializedName("frame_scope")
    private Boolean frameScope;
    @SerializedName("cumulate_type")
    private CumulateType cumulateType;

    @SerializedName("always_collect_memory")
    private boolean alwaysCollectMemory;
    @SerializedName("error_handling_mode")
    private ErrorHandlingMode errorHandlingMode;

    QuickConfigState(ProfilerType profiler, Boolean frameScope, CumulateType cumulateType, ErrorHandlingMode errorHandlingMode,
                     boolean alwaysCollectMemory) {
        this.profiler = profiler;
        this.frameScope = frameScope;
        this.cumulateType = cumulateType;
        this.errorHandlingMode = errorHandlingMode;
        this.alwaysCollectMemory = alwaysCollectMemory;
    }

    public ProfilerType getProfiler() {
        return this.profiler;
    }

    public Boolean getFrameScope() {
        return this.frameScope;
    }

    public Boolean getAlwaysCollectMemory() {
        return this.alwaysCollectMemory;
    }

    public CumulateType getComulateType() {
        return this.cumulateType;
    }

    public ErrorHandlingMode getErrorHandlingMode() {
        return this.errorHandlingMode;
    }
}
