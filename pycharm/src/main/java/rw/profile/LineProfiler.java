package rw.profile;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import rw.handler.Activable;
import rw.quickconfig.QuickConfig;
import rw.session.events.LineProfile;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


abstract public class LineProfiler implements Activable {
    protected Map<File, FileValues> values;
    Project project;
    QuickConfig quickConfig;
    ProfilePreviewRenderer previewRenderer;

    public LineProfiler(Project project, QuickConfig quickConfig) {
        this.quickConfig = quickConfig;
        this.project = project;
        this.values = new HashMap<>();
        this.previewRenderer = new ProfilePreviewRenderer(this.project, this);
    }

    public void onLineProfileEvent(LineProfile event) {
    }

    public Map<File, FileValues> getFileTimings() {
        return values;
    }

    @Nullable
    public Color getLineColor(File path, int line, Editor editor) {
        FileValues fileValues = this.values.get(path);
        if (fileValues == null) {
            return null;
        }

        return fileValues.getLineColor(line, editor, this.quickConfig.getState().getFrameScope(),
                this.quickConfig.getState().getComulateType());
    }

    @Nullable
    public Long getValue(File path, int line, Editor editor) {
        FileValues fileValues = this.values.get(path);
        if (fileValues == null) {
            return null;
        }

        Long ret = fileValues.getValue(line, editor, this.getQuickConfig().getState().getComulateType());
        return ret;
    }

    public void clearFile(File path) {
        FileValues fileValues = this.values.get(path);
        if (fileValues == null) {
            return;
        }
        fileValues.clear();
    }

    public void clearLines(File path, int start, int end) {
        FileValues fileValues = this.values.get(path);
        if (fileValues == null) {
            return;
        }
        fileValues.clear(start, end);
    }

    public void activate() {
        this.previewRenderer.activate();
    }

    public void deactivate() {
        this.previewRenderer.deactivate();
    }

    @VisibleForTesting
    public abstract String format(Long value);

    public void update() {
        this.previewRenderer.update();
    }

    public QuickConfig getQuickConfig() {
        return this.quickConfig;
    }
}
