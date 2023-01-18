package rw.session.events;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.AbstractProjectViewPane;
import rw.icons.IconPatcher;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

public class WatchingFiles extends FileEvent {
    public static final String ID = "WatchingFiles";

    private Set<String> files;

    public File getLocalPath() {
        return new File(this.handler.convertPathToLocal(this.path, false));
    }

    @Override
    public void handle() {
        this.handler.addWatched(this.files.stream().map(e -> new File(this.handler.convertPathToLocal(e, false))).collect(Collectors.toSet()));
        IconPatcher.refresh(this.handler.getProject());
    }
}
