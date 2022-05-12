package rw.preferences;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PreferencesState {
    public int blinkDuration;

    public PreferencesState() {
        this.blinkDuration = 300;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreferencesState that = (PreferencesState) o;

        boolean ret = true;

        ret &= this.blinkDuration == that.blinkDuration;
        return ret;
    }

    public static PreferencesState getInstance(@NotNull Project project) {
        return project.getService(PreferencesState.class);
    }
}