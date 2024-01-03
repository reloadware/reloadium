package rw.profile;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import rw.handler.Activable;
import rw.quickconfig.QuickConfig;
import rw.session.events.LineProfile;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;


abstract public class LineProfiler implements Activable {
    protected Map<VirtualFile, FileValues> values;
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

    public Map<VirtualFile, FileValues> getFileTimings() {
        return values;
    }

    @Nullable
    public Color getLineColor(@NotNull VirtualFile file, int line, Editor editor) {
        FileValues fileValues = this.values.get(file);
        if (fileValues == null) {
            return null;
        }

        return fileValues.getLineColor(line, editor, this.quickConfig.getState().getFrameScope(),
                this.quickConfig.getState().getComulateType());
    }

    @Nullable
    public Long getValue(@NotNull VirtualFile file, int line, Editor editor) {
        FileValues fileValues = this.values.get(file);
        if (fileValues == null) {
            return null;
        }

        Long ret = fileValues.getValue(line, editor, this.getQuickConfig().getState().getComulateType());
        return ret;
    }

    public void clearFile(@NotNull VirtualFile file) {
        FileValues fileValues = this.values.get(file);
        if (fileValues == null) {
            return;
        }
        fileValues.clear();
    }

    public void clearLines(@NotNull VirtualFile file, int start, int end) {
        FileValues fileValues = this.values.get(file);
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
