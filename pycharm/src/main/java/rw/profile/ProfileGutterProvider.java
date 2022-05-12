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
import com.intellij.ui.JBColor;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.frame.FileFrames;
import rw.frame.Frame;
import rw.frame.FrameManager;
import rw.handler.runConf.BaseRunConfHandler;
import rw.handler.runConf.RunConfHandlerManager;
import rw.service.Service;

import java.awt.*;
import java.io.File;
import java.util.List;

public class ProfileGutterProvider implements TextAnnotationGutterProvider {
    boolean selected;

    Editor editor;

    ProfileGutterProvider(Editor editor)
    {
        this.selected = false;
        this.editor = editor;
    }

    @Override
    public boolean useMargin() {
        return true;
    }

    @Override
    public @Nullable String getLineText(int line, Editor editor) {
        FileFrames frames = this.getFileFrames();
        String empty = "       ";

        if (frames == null) {
            return empty;
        }

        Float time = frames.getLineTimeMs(line+1);

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
        FileFrames frames = this.getFileFrames();

        if (frames == null) {
            return null;
        }

        Color color = frames.getLineColor(line+1);

        if (color == null) {
            return null;
        }

        Color ret = new Color(color.getRed(),
        color.getGreen(),
        color.getBlue(),
        70);
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

    @Nullable private FileFrames getFileFrames() {
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

        FrameManager frameManager = handler.getFrameManager();

        if (frameManager == null) {
            return null;
        }

        File path = ((EditorImpl)editor).getVirtualFile().toNioPath().toFile();
        FileFrames frames = frameManager.getForPath(path);
        return frames;
    }
}
