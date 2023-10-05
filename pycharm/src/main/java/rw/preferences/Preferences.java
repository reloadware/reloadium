package rw.preferences;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

@State(name = "ReloadiumPreferences", storages = @Storage("reloadium.xml"))
public class Preferences implements PersistentStateComponent<PreferencesState> {
    PreferencesState state = new PreferencesState();

    public static Preferences get() {
        return ApplicationManager.getApplication().getService(Preferences.class);
    }

    @NotNull
    @Override
    public PreferencesState getState() {
        return this.state;
    }

    @Override
    public void loadState(@NotNull PreferencesState state) {
        XmlSerializerUtil.copyBean(state, this.state);
    }
}