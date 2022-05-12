package rw.dialogs;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

@State(name = "DialogsState", storages = @Storage("reloadium.xml"))
public class DialogsState implements PersistentStateComponent<DialogsState> {
    public boolean firstRun = true;
    public boolean firstDebug = true;
    public boolean firstUserError = true;
    public boolean firstFrameError = true;

    public static DialogsState get() {
        DialogsState singleton = ApplicationManager.getApplication().getService(DialogsState.class);
        return singleton;
    }

    @NotNull
    @Override
    public DialogsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull DialogsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
