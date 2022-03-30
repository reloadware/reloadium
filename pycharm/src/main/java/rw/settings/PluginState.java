package rw.settings;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PluginState {
    public List<String> reloadiumPath;
    public boolean watchCwd;
    public boolean watchSourceRoots;
    public boolean printLogo;
    public boolean cacheEnabled;
    public boolean verbose;
    public boolean debuggerSpeedups;

    public PluginState() {
        this.reloadiumPath = new ArrayList<String>();
        this.watchCwd = true;
        this.watchSourceRoots = true;
        this.printLogo = true;
        this.cacheEnabled = true;
        this.verbose = true;
        this.debuggerSpeedups = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PluginState that = (PluginState) o;

        boolean ret = true;

        ret &= Objects.equals(this.reloadiumPath, that.reloadiumPath);
        ret &= this.cacheEnabled == that.cacheEnabled;
        ret &= this.debuggerSpeedups == that.debuggerSpeedups;
        ret &= this.watchCwd == that.watchCwd;
        ret &= this.printLogo == that.printLogo;
        ret &= this.verbose == that.verbose;
        ret &= this.watchSourceRoots == that.watchSourceRoots;
        return ret;
    }

    public static PluginState getInstance(@NotNull Project project) {
        return project.getService(PluginState.class);
    }
}