package rw.preferences;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.NlsContexts;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.consts.Const;

import javax.swing.*;

public class PreferencesConfigurable implements Configurable, SearchableConfigurable, Configurable.NoScroll {
    PreferencesForm form;

    PreferencesConfigurable() {
        this.form = new PreferencesForm();
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
        return !this.form.getState().equals(Preferences.getInstance().getState());
    }

    @Override
    public void apply() {
        Preferences.getInstance().loadState(this.form.getState());
    }

    @Override
    public void reset() {
        this.form.setState(Preferences.getInstance().getState());
    }

    @Override
    public @NotNull @NonNls String getId() {
        return Const.get().packageName;
    }
}
