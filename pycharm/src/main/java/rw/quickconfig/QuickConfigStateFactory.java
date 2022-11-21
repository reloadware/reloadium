package rw.quickconfig;

import rw.preferences.Preferences;
import rw.preferences.PreferencesState;

public class QuickConfigStateFactory {
    public static QuickConfigState create() {
        PreferencesState preferences = Preferences.getInstance().getState();

        QuickConfigState state = new QuickConfigState();
        state.profiler = preferences.defaultProfiler;
        return state;
    }
}
