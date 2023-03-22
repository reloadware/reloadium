package rw.highlights;

import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.util.ui.UIUtil;
import com.jetbrains.python.PythonLanguage;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


public class ErrorRenderer implements EditorCustomElementRenderer {
    String msg;
    List<String> lines;
    int width;

    ErrorRenderer(Editor e, String msg) {
        this.msg = msg;

        final CodeStyleSettings settings = CodeStyle.getSettings(e);
        this.width = settings.getRightMargin(PythonLanguage.getInstance());
        this.lines = this.splitStringIntoLines(this.msg, this.width);
    }

    @Override
    public int calcWidthInPixels(@NotNull Inlay inlay) {
        return this.width;
    }

    @Override
    public int calcHeightInPixels(@NotNull Inlay inlay) {
        return inlay.getEditor().getLineHeight() * this.lines.size();
    }

    private List<String> splitStringIntoLines(String input, int maxLineLength) {
        String[] words = input.split(" ");
        StringBuilder currentLine = new StringBuilder();
        List<String> lines = new ArrayList<>();

        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > maxLineLength) {
                lines.add(currentLine.toString());
                currentLine.setLength(0);
            }
            if (currentLine.length() > 0) {
                currentLine.append(" ");
            }
            currentLine.append(word);
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    @Override
    public void paint(@NotNull Inlay inlay, @NotNull Graphics g, @NotNull Rectangle targetRegion, @NotNull TextAttributes textAttributes) {
        Editor editor = inlay.getEditor();
        Document document = editor.getDocument();

        int lineNumber = document.getLineNumber(inlay.getOffset());

        int offsetStart = editor.getDocument().getLineStartOffset(lineNumber);
        int offsetEnd = editor.getDocument().getLineEndOffset(lineNumber);

        String text = editor.getDocument().getText(new TextRange(offsetStart, offsetEnd));
        String indentation = text.substring(0, text.length() - text.stripLeading().length());

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

        IntStream.range(0, this.lines.size())
                .forEach(index -> {
                            int offsetY = index * (editor.getLineHeight());
                            String line = lines.get(index);
                            g.drawString(line, currentX, currentY + editor.getAscent() + offsetY);
                        }
                );
    }
}
