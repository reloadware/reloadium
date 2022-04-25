package rw.dialogs;

import com.intellij.ui.BrowserHyperlinkListener;
import com.intellij.ui.ColorUtil;
import com.intellij.util.ui.JBUI;
import rw.consts.Const;

import javax.swing.*;
import java.awt.*;

public class HowToUseBody {
    private final Icon imageIcon;
    private final String descriptionValue;

    private JLabel image;

    private JLabel title;
    private JPanel main;
    private JLabel description;
    private JEditorPane termsAndConditions;

    HowToUseBody(Icon image, String description, String title, boolean showTerms) {
        super();
        this.imageIcon = image;
        this.descriptionValue = description;

        this.description.setText("<html>" + description + "</html>");

        String url = String.format("<a href=\"%s\">terms and conditions and privacy policy</a>", Const.get().legalUrl);
        String terms = "<div style=\"font-family:DejaVu Sans\">By using this software you accept " + url + "</div>";

        if (showTerms) {

            this.termsAndConditions.setText(terms);
            this.termsAndConditions.addHyperlinkListener(BrowserHyperlinkListener.INSTANCE);
            this.termsAndConditions.setBackground(ColorUtil.hackBrightness(
                    JBUI.CurrentTheme.CustomFrameDecorations.paneBackground(), 1, 1 / 1.05f)
            );
            this.termsAndConditions.setFont(this.description.getFont());
        } else {
            this.termsAndConditions.setVisible(false);
        }

        this.title.setText(title);
    }

    private void createUIComponents() {
        this.image = new JLabel(this.imageIcon);
        this.image.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public JPanel getMainPanel() {
        return this.main;
    }
}
