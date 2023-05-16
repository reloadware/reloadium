package rw.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

@State(name = "ReloadiumProjectSettings", storages = @Storage("reloadium.xml"))
public class ProjectSettings implements PersistentStateComponent<ProjectState> {
    ProjectState state = new ProjectState();

    public static ProjectSettings getInstance(@NotNull Project project) {
        return project.getService(ProjectSettings.class);
    }

    @NotNull
    @Override
    public ProjectState getState() {
        return this.state;
    }

    @Override
    public void loadState(@NotNull ProjectState state) {
        XmlSerializerUtil.copyBean(state, this.state);
    }
}