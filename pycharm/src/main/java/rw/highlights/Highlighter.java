package rw.highlights;

import com.intellij.diff.util.DiffUtil;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diff.LineStatusMarkerDrawUtil;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.VcsBundle;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.ui.DebuggerColors;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;


class GutterRenderer implements ActiveGutterRenderer {
    private final int lineno;
    private final int endLineno;
    private final Color color;
    private final @NlsContexts.Tooltip String tooltip;

    GutterRenderer(int lineno, int endLineno, Color color, @NlsContexts.Tooltip String tooltip) {
        this.lineno = lineno;
        this.endLineno = endLineno;
        this.color = color;
        this.tooltip = tooltip;
    }

    @Override
    public void paint(@NotNull Editor editor, @NotNull Graphics g, @NotNull Rectangle r) {
        LineStatusMarkerDrawUtil.paintSimpleRange(g, editor, lineno - 1, endLineno, color);
    }

    @Override
    public String getTooltipText() {
        return tooltip;
    }

    @Override
    public boolean canDoAction(@NotNull MouseEvent e) {
        return LineStatusMarkerDrawUtil.isInsideMarkerArea(e);
    }

    @Override
    public void doAction(@NotNull Editor editor, @NotNull MouseEvent e) {
    }

    @NotNull
    @Override
    public String getAccessibleName() {
        return VcsBundle.message("patch.apply.marker.renderer", getTooltipText());
    }
}

public class Highlighter {
    RangeHighlighter device;
    MarkupModel markupModel;

    int line;
    int endLine;
    VirtualFile file;
    Project project;
    Color color;
    int layer;
    boolean gutter;

    public Highlighter(Project project, @NotNull VirtualFile file, int line, int endLine, Color color, int layer, boolean gutter) {
        this.project = project;
        this.endLine = endLine;
        this.line = line;
        this.file = file;
        this.color = color;
        this.layer = layer;

        this.device = null;
        this.markupModel = null;

        this.gutter = gutter;
    }

    public Highlighter(Project project, @NotNull VirtualFile file, int line, Color color, int layer, boolean gutter) {
        this(project, file, line, line, color, layer, gutter);
    }

    public void hide() {
        if (this.device == null) {
            return;
        }
        this.markupModel.removeHighlighter(this.device);
        this.device = null;
    }

    public void show() {
        if (this.device != null) {
            return;
        }

        Document document = ReadAction.compute(() -> FileDocumentManager.getInstance().getDocument(this.file));

        this.markupModel = DocumentMarkupModel.forDocument(document, this.project, true);

        TextAttributes textAttribute;

        int layer;

        if (this.gutter) {
            textAttribute = null;
            layer = this.getJbLayer() + 50;
        } else {
            layer = this.getJbLayer();
            textAttribute = new TextAttributes(null, this.color,
                    null, null, Font.PLAIN);
        }

        try {
            if (this.line == this.endLine) {
                this.device = this.markupModel.addLineHighlighter(this.line - 1,
                        layer, textAttribute);
            } else {
                TextRange textRange = this.getTextRange(document);
                this.device = this.markupModel.addRangeHighlighter(textRange.getStartOffset(), textRange.getEndOffset(),
                        layer,
                        textAttribute, HighlighterTargetArea.LINES_IN_RANGE);
            }
            if (this.gutter) {
                LineMarkerRenderer renderer = new GutterRenderer(this.line, this.endLine, this.color, "");
                this.device.setLineMarkerRenderer(renderer);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    private int getJbLayer() {
        return DebuggerColors.EXECUTION_LINE_HIGHLIGHTERLAYER + this.layer;
    }

    @NotNull
    private TextRange getTextRange(Document document) {
        int correctedLineEnd = this.endLine;
        if (this.endLine == -1) {
            correctedLineEnd = this.line - 1;
        }

        if (correctedLineEnd < this.line) {
            correctedLineEnd = this.line;
        }

        TextRange range = DiffUtil.getLinesRange(document, this.line - 1, correctedLineEnd);
        return range;
    }

    public int getLine() {
        return line;
    }
}
