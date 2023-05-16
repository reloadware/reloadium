package rw.session.events;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;

public class FunctionTraced extends FileEvent {
    public static final String ID = "FunctionTraced";
    private static final Logger LOGGER = Logger.getInstance(ModuleUpdate.class);
    private String name;
    private int line;

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
