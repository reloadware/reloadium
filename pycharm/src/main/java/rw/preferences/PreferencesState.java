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

    public PreferencesState() {
        this.blinkDuration = 300;
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
        return ret;
    }

    public static PreferencesState getInstance(@NotNull Project project) {
        return project.getService(PreferencesState.class);
    }
}