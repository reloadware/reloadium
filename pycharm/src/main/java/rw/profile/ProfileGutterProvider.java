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
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import org.jetbrains.annotations.Nullable;
import rw.stack.Stack;
import rw.handler.runConf.BaseRunConfHandler;
import rw.handler.runConf.RunConfHandlerManager;

import java.awt.*;
import java.io.File;
import java.sql.Time;
import java.util.List;

public class ProfileGutterProvider implements TextAnnotationGutterProvider {
    boolean selected;

    Editor editor;
    File path;

    ProfileGutterProvider(Editor editor) {
        this.selected = false;
        this.editor = editor;
        this.path = ((EditorImpl) editor).getVirtualFile().toNioPath().toFile();
    }

    @Override
    public boolean useMargin() {
        return true;
    }

    @Override
    public @Nullable
    String getLineText(int line, Editor editor) {
        String empty = "       ";
        TimeProfiler timeProfiler = this.getTimeProfiler();

        if (timeProfiler == null) {
            return null;
        }

        Float time = timeProfiler.getLineTimeMs(this.path, line + 1);

        if (time == null) {
            return empty;
        }

        String ret = String.format("%.3f", time);
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
        TimeProfiler timeProfiler = this.getTimeProfiler();
        if (timeProfiler == null) {
            return null;
        }

        Color color = timeProfiler.getLineColor(this.path, line + 1);

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
    private TimeProfiler getTimeProfiler() {
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
        BaseRunConfHandler handler = RunConfHandlerManager.get().getCurrentHandler(project);

        if (handler == null) {
            return null;
        }


        return handler.getTimeProfiler();
    }
}
