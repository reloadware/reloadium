package rw.session.events;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;
import rw.dialogs.DialogsState;
import rw.icons.Icons;

import static com.intellij.openapi.ui.Messages.getOkButton;

public class ShowDialog extends Event {
    public static final String ID = "ShowDialog";
    private static final Logger LOGGER = Logger.getInstance(ShowDialog.class);

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

        if (!DialogsState.get().shouldShow(this.title)) {
            return;
        }

        ApplicationManager.getApplication().invokeLater(() -> {
            LOGGER.info("Showing dialog: " + this.title);

            DialogWrapper.DoNotAskOption.Adapter doNotAskOption = new DialogWrapper.DoNotAskOption.Adapter() {
                @Override
                public void rememberChoice(boolean isSelected, int exitCode) {
                  if (isSelected) {
                    DialogsState.get().setToBeShown(title, false);
                  }
                }

                @Override
                public @NotNull String getDoNotShowMessage() {
                    return "Do not show again";
                }
            };

            Messages.showDialog(this.handler.getProject(),
                    this.message,
                    this.title,
                    new String[]{getOkButton()},
                    0,
                    Icons.ProductBig,
                    doNotAskOption
                    );
        });
    }
}
