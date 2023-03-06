package rw.profile;

import com.intellij.diff.util.DiffUtil;
import com.intellij.execution.process.mediator.daemon.DaemonLaunchOptions;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diff.LineStatusMarkerDrawUtil;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.editor.FoldingModel;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.VcsBundle;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.ui.JBColor;
import com.intellij.xdebugger.ui.DebuggerColors;
import org.jetbrains.annotations.NotNull;
import rw.quickconfig.CumulateType;
import rw.quickconfig.QuickConfig;
import rw.stack.Stack;
import rw.highlights.Highlighter;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


class GutterRenderer implements ActiveGutterRenderer {
    FileValues fileValues;
    Color emptyColor;
    String tooltip;
    LineProfiler lineProfiler;

    GutterRenderer(FileValues fileValues, LineProfiler lineProfiler) {
        this.fileValues = fileValues;
        this.lineProfiler = lineProfiler;
        this.emptyColor = EditorColorsManager.getInstance().getGlobalScheme().getColor(EditorColors.GUTTER_BACKGROUND);
        this.tooltip = null;
    }

    @Override
    public void paint(@NotNull Editor editor, @NotNull Graphics g, @NotNull Rectangle r) {
        CumulateType cumulateType = this.lineProfiler.getQuickConfig().getState().getComulateType();
        Set<Integer> lines = new HashSet<>(this.fileValues.getValues(cumulateType).keySet());

        if (lines.isEmpty()) {
            return;
        }

        Integer minLine = Collections.min(lines);
        Integer maxLine = Collections.max(lines);

        for (Integer line : IntStream.rangeClosed(minLine - 1, maxLine).toArray()) {
            Color color = this.fileValues.getLineColor(line, editor,
                    this.lineProfiler.getQuickConfig().getState().getFrameScope(), cumulateType);
            if (color == null) {
                color = this.emptyColor;
            }
            int start = line;
            int end = line + 1;

            LineStatusMarkerDrawUtil.paintSimpleRange(g, editor, start, end, color);
        }
    }

    @Override
    public String getTooltipText() {
        return this.tooltip;
    }

    @Override
    public boolean canDoAction(@NotNull MouseEvent e) {
        CumulateType cumulateType = this.lineProfiler.getQuickConfig().getState().getComulateType();

        Editor editor;
        try {
            Method getEditor = e.getSource().getClass().getDeclaredMethod("getEditor");
            getEditor.setAccessible(true);
            editor = (Editor) getEditor.invoke(e.getSource());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }

        int offset = editor.logicalPositionToOffset(editor.xyToLogicalPosition(e.getPoint()));
        int line = editor.offsetToLogicalPosition(offset).line;

        Long value = this.fileValues.getValue(line, editor, cumulateType);
        if (value != null) {
            this.tooltip = this.lineProfiler.format(value);
        }

        return LineStatusMarkerDrawUtil.isInsideMarkerArea(e);
    }

    @Override
    public void doAction(@NotNull Editor editor, @NotNull MouseEvent e) {
        JPopupMenu popup = new JPopupMenu();

        JMenuItem item = new JMenuItem(this.getTooltipText());
        popup.add(item);

        popup.show(editor.getComponent(), e.getX() + 10, e.getY() - 30);

        javax.swing.Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popup.setVisible(false);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    @NotNull
    @Override
    public String getAccessibleName() {
        return VcsBundle.message("patch.apply.marker.renderer", getTooltipText());
    }
}


class FileValuesRenderer {
    File file;
    FileValues fileValues;
    RangeHighlighter device;
    MarkupModel markupModel;
    VirtualFile virtualFile;
    Document document;
    Project project;
    LineProfiler lineProfiler;

    FileValuesRenderer(Project project, File file, FileValues fileValues, LineProfiler lineProfiler) {
        this.file = file;
        this.fileValues = fileValues;
        this.project = project;
        this.lineProfiler = lineProfiler;

        this.virtualFile = new VirtualFileWrapper(this.file).getVirtualFile();
        this.document = ReadAction.compute(() -> FileDocumentManager.getInstance().getDocument(this.virtualFile));
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

        if(this.fileValues.isEmpty()) {
            return;
        }

        Integer minLine = Collections.min(this.fileValues.getValues(CumulateType.DEFAULT).keySet());
        Integer maxLine = Collections.max(this.fileValues.getValues(CumulateType.DEFAULT).keySet());

        ApplicationManager.getApplication().invokeLater(() -> {
            TextRange range = DiffUtil.getLinesRange(this.document, minLine - 1, maxLine);
            this.device = this.markupModel.addRangeHighlighter(null,
                    range.getStartOffset(), range.getEndOffset(),
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

    public void deactivate() {
        this.fileValuesRenderers.forEach(FileValuesRenderer::deactivate);
    }

    public void activate() {
        this.deactivate();

        this.fileValuesRenderers.clear();

        for (Map.Entry<File, FileValues> pathToFileTiming : this.lineProfiler.getFileTimings().entrySet()) {
            FileValuesRenderer fileValuesRenderer = new FileValuesRenderer(this.project,
                    pathToFileTiming.getKey(),
                    pathToFileTiming.getValue(),
                    this.lineProfiler);
            this.fileValuesRenderers.add(fileValuesRenderer);
            fileValuesRenderer.activate();
        }
    }

    public void update() {
        this.deactivate();
        this.activate();
    }
}