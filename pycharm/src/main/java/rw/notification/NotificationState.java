package rw.notification;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@State(name = "NotificationState", storages = @Storage("reloadium.xml"))
public class NotificationState implements PersistentStateComponent<NotificationState> {
    public Map<String, Boolean> toBeShown = new HashMap<>();

    public static NotificationState get() {
        NotificationState singleton = ApplicationManager.getApplication().getService(NotificationState.class);
        return singleton;
    }

    @NotNull
    @Override
    public NotificationState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull NotificationState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public void setToBeShown(String key, Boolean value) {
        this.toBeShown.put(key, value);
    }

    public Boolean shouldShow(String key) {
        return this.toBeShown.getOrDefault(key, true);
    }
}
