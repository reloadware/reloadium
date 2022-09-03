package rw.preferences;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import rw.util.colormap.ColorMap;
import rw.util.colormap.ColorMaps;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PreferencesState {
    public int blinkDuration;
    public String timingColorMap;
    public boolean sentry;
    public boolean telemetry;

    public PreferencesState() {
        this.blinkDuration = 1000;
        this.telemetry = true;
        this.sentry = true;
        this.timingColorMap = ColorMaps.get().viridis.getName();
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
        ret &= this.telemetry == that.telemetry;
        return ret;
    }
}