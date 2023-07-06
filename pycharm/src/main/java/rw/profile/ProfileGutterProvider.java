package rw.profile;

import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.TextAnnotationGutterProvider;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import org.jetbrains.annotations.Nullable;
import rw.handler.RunConfHandler;
import rw.handler.RunConfHandlerManager;

import java.awt.*;
import java.util.List;

public class ProfileGutterProvider implements TextAnnotationGutterProvider {
    boolean selected;

    Editor editor;
    VirtualFile file;
    Long maxValue;
    Long minValue;

    ProfileGutterProvider(Editor editor) {
        this.selected = false;
        this.editor = editor;
        this.file = ((EditorImpl) editor).getVirtualFile();

        this.maxValue = 0L;
        this.minValue = Long.MAX_VALUE;
    }

    @Override
    public boolean useMargin() {
        return true;
    }

    @Override
    public @Nullable
    String getLineText(int line, Editor editor) {
        LineProfiler lineProfiler = this.getLineProfiler();

        if (lineProfiler == null) {
            return null;
        }

        String empty = "       ";
        Long value = this.getLineValue(line);

        if (value == null) {
            return empty;
        }

        String ret = String.format("%10s", lineProfiler.format(value));
        return ret;
    }

    @Nullable
    private Long getLineValue(int line) {
        LineProfiler lineProfiler = this.getLineProfiler();
        if (lineProfiler == null) {
            return null;
        }

        Long ret = lineProfiler.getValue(this.file, line, this.editor);
        return ret;
    }

    @Override
    @Nullable
    public @NlsContexts.Tooltip String getToolTip(int line, Editor editor) {
        return null;
    }

    @Override
    public EditorFontType getStyle(int line, Editor editor) {
        return null;
    }

    @Override
    @Nullable
    public ColorKey getColor(int line, Editor editor) {
        return EditorColors.ANNOTATIONS_COLOR;
    }

    @Override
    @Nullable
    public Color getBgColor(int line, Editor editor) {
        LineProfiler lineProfiler = this.getLineProfiler();
        if (lineProfiler == null) {
            return null;
        }

        Color color = lineProfiler.getLineColor(this.file, line, editor);

        if (color == null) {
            return null;
        }

        Color ret = new Color(color.getRed(),
                color.getGreen(),
                color.getBlue(),
                100);
        return ret;
    }

    @Override
    public List<AnAction> getPopupActions(int line, Editor editor) {
        return null;
    }

    @Override
    public void gutterClosed() {
        this.selected = false;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Nullable
    private LineProfiler getLineProfiler() {
        Project project = editor.getProject();

        if (project == null) {
            return null;
        }

        XDebugSessionImpl debugSession = ((XDebugSessionImpl) XDebuggerManager.getInstance(editor.getProject()).getCurrentSession());

        if (debugSession == null) {
            return null;
        }

        ExecutionEnvironment environment = debugSession.getExecutionEnvironment();

        if (environment == null) {
            return null;
        }
        RunConfHandler handler = RunConfHandlerManager.get().getCurrentDebugHandler(project);

        if (handler == null) {
            return null;
        }

        return handler.getActiveProfiler();
    }
}
