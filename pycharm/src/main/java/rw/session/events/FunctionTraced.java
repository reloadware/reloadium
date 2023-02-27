package rw.session.events;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import rw.highlights.Blink;
import rw.highlights.Blinker;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;

import java.awt.*;

public class FunctionTraced extends FileEvent {
    public static final String ID = "FunctionTraced";

    private String name;
    private int line;

    private static final Logger LOGGER = Logger.getInstance(ModuleUpdate.class);

    @Override
    public void handle() {
        super.handle();
        LOGGER.info("Handling FunctionTraced");

        ApplicationManager.getApplication().invokeLater(() -> {
            this.handler.getFastDebug().onFunctionTracedEvent(this);
        });
    }

    public String getName() {
        return this.name;
    }

    public int getLine() {
        return this.line;
    }
}
