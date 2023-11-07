package rw.preferences;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.consts.Const;

import javax.swing.*;

public class PreferencesConfigurable implements Configurable, SearchableConfigurable, Configurable.NoScroll {
    PreferencesForm form;

    public PreferencesConfigurable() {
        this.form = new PreferencesForm();
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return StringUtil.capitalize(Const.get().packageName) + "Preferences";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return this.form.getMainPanel();
    }

    @Override
    public boolean isModified() {
        return !this.form.getState().equals(Preferences.get().getState());
    }

    @Override
    public void apply() {
        Preferences.get().loadState(this.form.getState());
    }

    @Override
    public void reset() {
        this.form.setState(Preferences.get().getState());
    }

    @Override
    public @NotNull @NonNls String getId() {
        return Const.get().packageName;
    }
}
