package rw.session.events;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;
import rw.icons.Icons;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static com.intellij.util.ui.UIUtil.invokeLaterIfNeeded;

public class ShowDialog extends Event {
    public static final String ID = "ShowDialog";
    private static final Logger LOGGER = Logger.getInstance(ModuleUpdate.class);

    String title;
    String message;

    @VisibleForTesting
    public ShowDialog(@NotNull String title, @NotNull String message) {
        this.title = title;
        this.message = message;
    }

    @Override
    public void handle() {
        super.handle();

        invokeLaterIfNeeded(() -> Messages.showMessageDialog(this.handler.getProject(), this.message, this.title, Icons.ProductBig));
    }
}
