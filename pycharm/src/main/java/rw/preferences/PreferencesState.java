package rw.preferences;

import rw.quickconfig.CumulateType;
import rw.quickconfig.ErrorHandlingMode;
import rw.quickconfig.ProfilerType;
import rw.util.colormap.ColorMaps;

public class PreferencesState {
    public int blinkDuration;
    public String timingColorMap;
    public ProfilerType defaultProfiler;
    public boolean defaultFrameScope;
    public CumulateType defaultCumulateType;
    public ErrorHandlingMode defaultErrorHandlingMode;
    public boolean alwaysCollectMemory;

    public boolean markReloadable;
    public boolean runtimeCompletion;

    public PreferencesState() {
        this.blinkDuration = 1000;
        this.markReloadable = true;
        this.timingColorMap = ColorMaps.get().viridis.getName();
        this.defaultProfiler = ProfilerType.DEFAULT;
        this.defaultFrameScope = true;
        this.defaultCumulateType = CumulateType.DEFAULT;
        this.defaultErrorHandlingMode = ErrorHandlingMode.DEFAULT;
        this.alwaysCollectMemory = false;
        this.runtimeCompletion = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreferencesState that = (PreferencesState) o;

        boolean ret;
        ret = this.blinkDuration == that.blinkDuration;
        ret &= this.timingColorMap.equals(that.timingColorMap);
        ret &= this.markReloadable == that.markReloadable;
        ret &= this.defaultProfiler == that.defaultProfiler;
        ret &= this.defaultFrameScope == that.defaultFrameScope;
        ret &= this.defaultCumulateType == that.defaultCumulateType;
        ret &= this.defaultErrorHandlingMode == that.defaultErrorHandlingMode;
        ret &= this.alwaysCollectMemory == that.alwaysCollectMemory;
        ret &= this.runtimeCompletion == that.runtimeCompletion;
        return ret;
    }
}