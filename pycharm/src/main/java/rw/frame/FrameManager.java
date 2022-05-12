package rw.frame;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.Nullable;
import rw.highlights.Blink;
import rw.highlights.Blinker;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;
import rw.profile.ProfilePreviewRenderer;
import rw.session.FrameProgress;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FrameManager {
    Map<File, FileFrames> pathToFrames;

    Project project;

    public FrameManager(Project project) {
        this.project = project;
        this.pathToFrames = new HashMap<>();
    }

    public void onFrameProgressEvent(FrameProgress event) {
        this.pathToFrames.putIfAbsent(event.getLocalPath(), new FileFrames(event.getLocalPath()));
        FileFrames frames = this.pathToFrames.get(event.getLocalPath());

        frames.onFrameProgressEvent(this.project, event);
    }

    @Nullable public FileFrames getForPath(File file) {
        return this.pathToFrames.get(file);
    }

    public Map<File, FileFrames> getPathToFrames() {
        return pathToFrames;
    }
}
