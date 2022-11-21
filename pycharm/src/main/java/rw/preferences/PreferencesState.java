package rw.preferences;

import rw.quickconfig.ProfilerType;
import rw.util.colormap.ColorMaps;

public class PreferencesState {
    public int blinkDuration;
    public String timingColorMap;
    public ProfilerType defaultProfiler;
    public boolean sentry;
    public boolean telemetry;
    public boolean markReloadable;

    public PreferencesState() {
        this.blinkDuration = 1000;
        this.telemetry = true;
        this.sentry = true;
        this.markReloadable = true;
        this.timingColorMap = ColorMaps.get().viridis.getName();
        this.defaultProfiler = ProfilerType.TIME;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreferencesState that = (PreferencesState) o;

        boolean ret;
        ret = this.blinkDuration == that.blinkDuration;
        ret &= this.timingColorMap.equals(that.timingColorMap);
        ret &= this.sentry == that.sentry;
        ret &= this.markReloadable == that.markReloadable;
        ret &= this.telemetry == that.telemetry;
        ret &= this.defaultProfiler == that.defaultProfiler;
        return ret;
    }
}