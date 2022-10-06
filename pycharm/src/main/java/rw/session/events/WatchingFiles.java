package rw.session.events;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.AbstractProjectViewPane;
import rw.icons.IconPatcher;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

public class WatchingFiles extends FileEvent {
    public static final String ID = "WatchingFiles";
    public static final String VERSION = "0.1.0";

    private Set<String> files;

    @Override
    public void handle() {
        this.handler.addWatched(this.files.stream().map(e -> new File(this.handler.convertPathToLocal(e))).collect(Collectors.toSet()));
        IconPatcher.refresh(this.handler.getProject());
    }
}
