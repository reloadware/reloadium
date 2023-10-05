package rw.dialogs;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.UIBundle;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;


public abstract class TipDialog extends DialogWrapper {
    private static final Logger LOGGER = Logger.getInstance(TipDialog.class);
    private final boolean showTerms;

    boolean result;
    String description;
    String title;
    Icon image;

    public TipDialog(@Nullable Project project, String title, String description, Icon image,
                     boolean showTerms) {
        super(project, false);

        this.result = true;
        this.description = description;
        this.title = title;
        this.image = image;
        this.showTerms = showTerms;

        setResizable(false);
        setTitle(title);

        TipDialog This = this;

        this.setDoNotAskOption(new com.intellij.openapi.ui.DoNotAskOption() {
            @Override
            public boolean isToBeShown() {
                return true;
            }

            @Override
            public void setToBeShown(boolean value, int exitCode) {
                This.onSetToBeShown(value, exitCode);
            }

            @Override
            public boolean canBeHidden() {
                return true;
            }

            @Override
            public boolean shouldSaveOptionsOnCancel() {
                return true;
            }

            @NotNull
            @Override
            public String getDoNotShowMessage() {
                return UIBundle.message("dialog.options.do.not.show");
            }
        });

        init();
    }

    private static JBFont getDefaultTextFont() {
        return JBFont.label().lessOn(1);
    }

    private static void addEmptyLine(Box box) {
        box.add(Box.createVerticalStrut(18));
    }

    private static JLabel label(@NlsContexts.Label String text, JBFont font) {
        JBLabel label = new JBLabel(text).withFont(font);
        label.setCopyable(true);
        label.setAllowAutoWrapping(true);
        return label;
    }

    abstract protected void onSetToBeShown(boolean value, int exitCode);

    abstract protected boolean shouldBeShown();

    @Override
    protected void doOKAction() {
        super.doOKAction();
        this.result = true;
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
        this.result = false;
    }

    public boolean getResult() {
        return this.result;
    }

    @Override
    public void show() {
        if (!this.shouldBeShown()) {
            return;
        }
        super.show();
    }

    @Override
    protected @Nullable
    JComponent createCenterPanel() {
        HowToUseBody body = new HowToUseBody(this.image, this.description, this.title, this.showTerms);

        BorderLayoutPanel ret = JBUI.Panels.simplePanel();
        ret.addToCenter(body.getMainPanel());

        return ret;
    }

    @Override
    protected JComponent createSouthPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(super.createSouthPanel(), BorderLayout.CENTER);
        return panel;
    }
}
