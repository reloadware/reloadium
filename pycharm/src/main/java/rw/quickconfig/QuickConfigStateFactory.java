package rw.quickconfig;

import rw.preferences.Preferences;
import rw.preferences.PreferencesState;

public class QuickConfigStateFactory {
    public static QuickConfigState create() {
        PreferencesState preferences = Preferences.get().getState();

        QuickConfigState state = new QuickConfigState(preferences.defaultProfiler,
                preferences.defaultFrameScope, preferences.defaultCumulateType,
                preferences.defaultErrorHandlingMode,
                preferences.alwaysCollectMemory);
        return state;
    }
}
