package rw.session.events;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebugSessionImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UpdateDebugger extends DebuggerEvent {
    public static final String ID = "UpdateDebugger";
    private static final Logger LOGGER = Logger.getInstance(ModuleUpdate.class);

    @Override
    public void handle() {
        super.handle();

        XDebugSessionImpl debugSession = ((XDebugSessionImpl) XDebuggerManager.getInstance(this.handler.getProject()).getCurrentSession());

        if (debugSession == null) {
            return;
        }

        try {
            Method clearPausedData = XDebugSessionImpl.class.getDeclaredMethod("clearPausedData");
            clearPausedData.setAccessible(true);
            clearPausedData.invoke(debugSession);
            debugSession.rebuildViews();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
