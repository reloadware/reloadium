package rw.util;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;

public class Stylesheet {
    static public void applyParentsStylesheet(JPanel parent, JEditorPane editorPane) {
        Font parentFont = parent.getFont();
        Color parentColor = parent.getForeground();

        StyleSheet styleSheet = new StyleSheet();
        styleSheet.addRule(String.format(
                "body { font-family: %s; font-size: %dpt; color: rgb(%d, %d, %d); }",
                parentFont.getFamily(),
                parentFont.getSize() + 1,
                parentColor.getRed(),
                parentColor.getGreen(),
                parentColor.getBlue()
        ));

        styleSheet.addRule("a { color: #2389f7; text-decoration: underline; }");

        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        htmlEditorKit.setStyleSheet(styleSheet);
        editorPane.setEditorKit(htmlEditorKit);
    }
}
