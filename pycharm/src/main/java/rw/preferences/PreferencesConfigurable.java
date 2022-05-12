package rw.preferences;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.consts.Const;

import javax.swing.*;

public class PreferencesConfigurable implements Configurable, SearchableConfigurable, Configurable.NoScroll {
    PreferencesForm form;
    Preferences preferences;

    PreferencesConfigurable() {
        this.form = new PreferencesForm();
        this.preferences = Preferences.getInstance();
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return StringUtils.capitalize(Const.get().packageName) + "Preferences";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return this.form.getMainPanel();
    }

    @Override
    public boolean isModified() {
        return !this.form.getState().equals(this.preferences.getState());
    }

    @Override
    public void apply() throws ConfigurationException {
        this.preferences.loadState(this.form.getState());
    }

    @Override
    public void reset() {
        this.form.setState(this.preferences.getState());
    }

    @Override
    public @NotNull @NonNls String getId() {
        return Const.get().packageName;
    }
}
