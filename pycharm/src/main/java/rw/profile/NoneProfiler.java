package rw.profile;


import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.VisibleForTesting;
import rw.quickconfig.QuickConfig;
import rw.session.events.LineProfile;

public class NoneProfiler extends LineProfiler {
    public NoneProfiler(Project project, QuickConfig quickConfig) {
        super(project, quickConfig);
    }

    @VisibleForTesting
    public String format(Long value) {
        return "";
    }

    public void onLineProfileEvent(LineProfile event) {
    }

    public void activate() {
    }
}
