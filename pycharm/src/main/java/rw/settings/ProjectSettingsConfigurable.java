package rw.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.consts.Const;

import javax.swing.*;

public class ProjectSettingsConfigurable implements Configurable, SearchableConfigurable, Configurable.NoScroll {
    Project project;
    ProjectSettingsForm form;
    ProjectSettings projectSettings;

    ProjectSettingsConfigurable(Project project) {
        this.project = project;

        this.form = new ProjectSettingsForm();
        this.projectSettings = ProjectSettings.getInstance(this.project);
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return StringUtil.capitalize(Const.get().packageName) + "ProjectSettings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return this.form.getMainPanel();
    }

    @Override
    public boolean isModified() {
        return !this.form.getState().equals(this.projectSettings.getState());
    }

    @Override
    public void apply() throws ConfigurationException {
        this.projectSettings.loadState(this.form.getState());
    }

    @Override
    public void reset() {
        this.form.setState(this.projectSettings.getState());
    }

    @Override
    public @NotNull @NonNls String getId() {
        return Const.get().packageName;
    }
}
