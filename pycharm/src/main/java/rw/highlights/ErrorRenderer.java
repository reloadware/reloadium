package rw.highlights;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.TextRange;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ErrorRenderer implements EditorCustomElementRenderer {
    String msg;

    ErrorRenderer(String msg) {
        this.msg = msg;
    }

    @Override
    public int calcWidthInPixels(@NotNull Inlay inlay) {
        return 100;
    }

    @Override
    public int calcHeightInPixels(@NotNull Inlay inlay) {
        return inlay.getEditor().getLineHeight();
    }

    @Override
    public void paint(@NotNull Inlay inlay, @NotNull Graphics g, @NotNull Rectangle targetRegion, @NotNull TextAttributes textAttributes) {
        Editor editor = inlay.getEditor();
        Document document = editor.getDocument();

        int lineNumber = document.getLineNumber(inlay.getOffset());

        int offsetStart = editor.getDocument().getLineStartOffset(lineNumber);
        int offsetEnd = editor.getDocument().getLineEndOffset(lineNumber);

        String line = editor.getDocument().getText(new TextRange(offsetStart, offsetEnd));
        String indentation = line.substring(0, line.length() - line.stripLeading().length());

        Color textColor = Color.RED;
        g.setColor(textColor);

        EditorColorsScheme colorsScheme = editor.getColorsScheme();

        Font defaultFont = UIUtil.getFontWithFallback(colorsScheme.getEditorFontName(), Font.PLAIN, colorsScheme.getEditorFontSize());
        FontMetrics metrics = g.getFontMetrics(defaultFont);

        int fontSize = colorsScheme.getEditorFontSize() - 1;
        if (fontSize < 11) {
            fontSize = 11;
        }

        Font font = UIUtil.getFontWithFallback(colorsScheme.getEditorFontName(), Font.PLAIN, fontSize);
        g.setFont(font);

        Point p = targetRegion.getLocation();

        int currentX = p.x + metrics.stringWidth(indentation);
        int currentY = p.y;

        g.drawString(this.msg, currentX, currentY + editor.getAscent());
    }
}
