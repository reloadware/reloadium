package rw.profile;


import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.VisibleForTesting;
import rw.quickconfig.QuickConfig;
import rw.session.events.LineProfile;

public class MemoryProfiler extends LineProfiler {
    public MemoryProfiler(Project project, QuickConfig quickConfig) {
        super(project, quickConfig);
    }

    @VisibleForTesting
    public String format(Long value) {
        if (value >= 1_000_000_000) {
            return String.format("%.3f GB ", value / 1e9);
        } else if (value >= 1_000_000) {
            return String.format("%.3f MB", value / 1e6);
        } else if (value >= 1_000) {
            return String.format("%.3f KB", value / 1e3);
        } else {
            return String.format("%d B ", value);
        }
    }

    public void onLineProfileEvent(LineProfile event) {
        super.onLineProfileEvent(event);

        this.values.putIfAbsent(event.getFile(), new FileValues());

        FileValues fileValues = this.values.get(event.getFile());
        fileValues.update(event.getMemoryValues(), event.getFrameUuid(), event.isPartial());
    }
}
