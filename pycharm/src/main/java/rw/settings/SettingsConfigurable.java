package rw.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.config.Config;

import javax.swing.*;

public class SettingsConfigurable implements Configurable, SearchableConfigurable, Configurable.NoScroll {
    Project project;
    SettingsForm form;
    Settings settings;

    SettingsConfigurable(Project project) {
        this.project = project;

        this.form = new SettingsForm();
        this.settings = Settings.getInstance(this.project);
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return StringUtils.capitalize(Config.get().packageName);
    }

    @Override
    public @Nullable JComponent createComponent() {
        return this.form.getMainPanel();
    }

    @Override
    public boolean isModified() {
        return !this.form.getState().equals(this.settings.getState());
    }

    @Override
    public void apply() throws ConfigurationException {
        this.settings.loadState(this.form.getState());
    }

    @Override
    public void reset() {
        this.form.setState(this.settings.getState());
    }

    @Override
    public @NotNull @NonNls String getId() {
        return Config.get().packageName;
    }
}
