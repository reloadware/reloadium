package rw.profile;

import com.intellij.diff.util.DiffUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diff.LineStatusMarkerDrawUtil;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.ScrollingModel;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.VcsBundle;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.IntPair;
import com.intellij.xdebugger.ui.DebuggerColors;
import org.jetbrains.annotations.NotNull;
import rw.quickconfig.CumulateType;
import rw.util.NewUI;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.stream.IntStream;

import static com.intellij.diff.util.DiffDrawUtil.lineToY;


class GutterRenderer implements ActiveGutterRenderer {
    FileValues fileValues;
    String tooltip;
    LineProfiler lineProfiler;

    GutterRenderer(FileValues fileValues, LineProfiler lineProfiler) {
        this.fileValues = fileValues;
        this.lineProfiler = lineProfiler;
        this.tooltip = null;
    }

    @Override
    public void paint(@NotNull Editor editor, @NotNull Graphics g, @NotNull Rectangle r) {
        CumulateType cumulateType = this.lineProfiler.getQuickConfig().getState().getComulateType();
        Set<Integer> lines = new HashSet<>(this.fileValues.getValues(cumulateType).keySet());

        if (lines.isEmpty()) {
            return;
        }

        ScrollingModel scrollingModel = editor.getScrollingModel();
        Rectangle visibleArea = scrollingModel.getVisibleArea();

        // Convert the corners of the visible area to logical positions
        LogicalPosition startLogicalPos = editor.xyToLogicalPosition(new Point(visibleArea.x, visibleArea.y));
        LogicalPosition endLogicalPos = editor.xyToLogicalPosition(new Point(visibleArea.x, visibleArea.y + visibleArea.height));

        // Convert logical positions to offsets
        int startOffset = editor.logicalPositionToOffset(startLogicalPos);
        int endOffset = editor.logicalPositionToOffset(endLogicalPos);

        // Convert offsets to line numbers
        int startLine = editor.getDocument().getLineNumber(startOffset);
        int endLine = editor.getDocument().getLineNumber(endOffset);

        for (Integer line : IntStream.rangeClosed(startLine - 1, endLine).toArray()) {
            Color color = this.fileValues.getLineColor(line, editor,
                    this.lineProfiler.getQuickConfig().getState().getFrameScope(), cumulateType);
            if (color == null) {
                continue;
            }
            int start = line;
            int end = line + 1;

            IntPair horizontalArea = LineStatusMarkerDrawUtil.getGutterArea(editor);

            int x, endX;

            if (NewUI.isEnabled()) {
                x = horizontalArea.first - 6;
                endX = horizontalArea.first - 3;
            } else {
                x = horizontalArea.first - 5;
                endX = horizontalArea.first;
            }

            int y = lineToY(editor, start);
            int endY = lineToY(editor, end);

            LineStatusMarkerDrawUtil.paintRect((Graphics2D) g, color, null, x, y, endX, endY);
        }
    }

    @Override
    public String getTooltipText() {
        return this.tooltip;
    }

    @Override
    public boolean canDoAction(@NotNull MouseEvent e) {
        return false;
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


class FileValuesRenderer {
    FileValues fileValues;
    RangeHighlighter device;
    MarkupModel markupModel;
    VirtualFile file;
    Document document;
    Project project;
    LineProfiler lineProfiler;

    FileValuesRenderer(Project project, @NotNull VirtualFile file, FileValues fileValues, LineProfiler lineProfiler) {
        this.fileValues = fileValues;
        this.file = file;
        this.project = project;
        this.lineProfiler = lineProfiler;
        this.document = ReadAction.compute(() -> FileDocumentManager.getInstance().getDocument(this.file));
        this.markupModel = DocumentMarkupModel.forDocument(this.document, this.project, true);
    }

    public void deactivate() {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (this.device == null) {
                return;
            }
            this.markupModel.removeHighlighter(this.device);
            this.device = null;
        });
    }

    public void activate() {
        this.deactivate();

        if (this.fileValues.isEmpty()) {
            return;
        }

        ApplicationManager.getApplication().invokeLater(() -> {
            int endOffset = this.document.getLineEndOffset(this.document.getLineCount() - 1);

            this.device = this.markupModel.addRangeHighlighter(null,
                    0, endOffset,
                    DebuggerColors.EXECUTION_LINE_HIGHLIGHTERLAYER + 51,
                    HighlighterTargetArea.LINES_IN_RANGE);
            LineMarkerRenderer renderer = new GutterRenderer(this.fileValues, this.lineProfiler);
            this.device.setLineMarkerRenderer(renderer);
        });
    }
}

public class ProfilePreviewRenderer {
    Project project;
    LineProfiler lineProfiler;
    List<FileValuesRenderer> fileValuesRenderers;

    public ProfilePreviewRenderer(Project project, LineProfiler lineProfiler) {
        this.project = project;
        this.lineProfiler = lineProfiler;

        this.fileValuesRenderers = new ArrayList<>();
    }

    synchronized public void deactivate() {
        this.fileValuesRenderers.forEach(FileValuesRenderer::deactivate);
    }

    synchronized public void activate() {
        this.deactivate();

        this.fileValuesRenderers.clear();

        for (Map.Entry<VirtualFile, FileValues> pathToFileTiming : this.lineProfiler.getFileTimings().entrySet()) {
            FileValuesRenderer fileValuesRenderer = new FileValuesRenderer(this.project,
                    pathToFileTiming.getKey(),
                    pathToFileTiming.getValue(),
                    this.lineProfiler);
            this.fileValuesRenderers.add(fileValuesRenderer);
            fileValuesRenderer.activate();
        }
    }

    synchronized public void update() {
        this.deactivate();
        this.activate();
    }
}