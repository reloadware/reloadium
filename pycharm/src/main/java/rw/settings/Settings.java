package rw.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

@State(name = "Reloadium", storages = @Storage("reloadium.xml"))
public class Settings implements PersistentStateComponent<PluginState> {
    PluginState state = new PluginState();
    public static Settings getInstance(@NotNull Project project) {
        return project.getService(Settings.class);
    }

    @NotNull
    @Override
    public PluginState getState() {
        return this.state;
    }

    @Override
    public void loadState(@NotNull PluginState state) {
        XmlSerializerUtil.copyBean(state, this.state);
    }
}