package rw.handler.runConf;

import com.google.gson.Gson;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.ex.temp.TempFileSystem;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import org.jetbrains.annotations.Nullable;
import rw.action.RunType;
import rw.audit.RwSentry;
import rw.consts.Const;
import rw.icons.IconPatcher;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;
import rw.quickconfig.QuickConfigStateFactory;
import rw.session.cmds.QuickConfigCmd;
import rw.settings.ProjectState;
import rw.settings.ProjectSettings;
import rw.util.EnvUtils;

import java.util.ArrayList;
import java.util.List;


public class PythonRunConfHandler extends BaseRunConfHandler {
    AbstractPythonRunConfiguration<?> runConf;
    AbstractPythonRunConfiguration<?> origRunConf;

    public static final String IDE_NAME_ENV = "RW_IDE_NAME";  //  # RwRender: public static final String IDE_NAME_ENV = "{{ ctx.env_vars.ide.name }}";  //
    public static final String IDE_VERSION_ENV = "RW_IDE_VERSION";  //  # RwRender: public static final String IDE_VERSION_ENV = "{{ ctx.env_vars.ide.version }}";  //
    public static final String IDE_PLUGIN_VERSION_ENV = "RW_IDE_PLUGINVERSION";  //  # RwRender: public static final String IDE_PLUGIN_VERSION_ENV = "{{ ctx.env_vars.ide.plugin_version }}";  //
    public static final String IDE_TYPE_ENV = "RW_IDE_TYPE";  //  # RwRender: public static final String IDE_TYPE_ENV = "{{ ctx.env_vars.ide.type }}";  //
    public static final String IDE_SERVER_PORT_ENV = "RW_IDE_SERVERPORT";  //  # RwRender: public static final String IDE_SERVER_PORT_ENV = "{{ ctx.env_vars.ide.server_port }}";  //

    public static final String DEBUGGER_SPEEDUPS_ENV = "RW_DEBUGGERSPEEDUPS";  //  # RwRender: public static final String DEBUGGER_SPEEDUPS_ENV = "{{ ctx.env_vars.misc.debugger_speedups }}";  //
    public static final String VERBOSE_ENV = "RW_VERBOSE";  //  # RwRender: public static final String VERBOSE_ENV = "{{ ctx.env_vars.misc.verbose }}";  //
    public static final String CACHE_ENV = "RW_CACHE";  //  # RwRender: public static final String CACHE_ENV = "{{ ctx.env_vars.misc.cache }}";  //
    public static final String TELEMETRY_ENV = "RW_TELEMETRY";  //  # RwRender: public static final String TELEMETRY_ENV = "{{ ctx.env_vars.misc.telemetry }}";  //
    public static final String SENTRY_ENV = "RW_SENTRY";  //  # RwRender: public static final String SENTRY_ENV = "{{ ctx.env_vars.misc.sentry }}";  //
    public static final String PRINT_LOGO_ENV = "RW_PRINTLOGO";  //  # RwRender: public static final String PRINT_LOGO_ENV = "{{ ctx.env_vars.misc.print_logo }}";  //
    public static final String WATCHCWD_ENV = "RW_WATCHCWD";  //  # RwRender: public static final String WATCHCWD_ENV = "{{ ctx.env_vars.misc.watch_cwd }}";  //
    public static final String RELOADIUMPATH_ENV = "RELOADIUMPATH";  //  # RwRender: public static final String RELOADIUMPATH_ENV = "{{ ctx.env_vars.misc.reloadiumpath }}";  //
    public static final String RELOADIUMIGNORE_ENV = "RELOADIUMIGNORE";  //  # RwRender: public static final String RELOADIUMIGNORE_ENV = "{{ ctx.env_vars.misc.reloadiumignore }}";  //
    public static final String TIME_PROFILE_ENV = "RW_TIMEPROFILE";  //  # RwRender: public static final String TIME_PROFILE_ENV = "{{ ctx.env_vars.misc.time_profile }}";  //
    public static final String QUICK_CONFIG_ENV = "RW_QUICKCONFIG";  //  # RwRender: public static final String QUICK_CONFIG_ENV = "{{ ctx.env_vars.misc.quick_config }}";  //

    public PythonRunConfHandler(RunConfiguration runConf) {
        super(runConf);
        this.runConf = (AbstractPythonRunConfiguration<?>) runConf;
        this.origRunConf = (AbstractPythonRunConfiguration<?>) runConf.clone();
    }

    @Override
    public void beforeRun(RunType runType) {
        super.beforeRun(runType);

        PreferencesState preferences = Preferences.getInstance().getState();
        String command;

        this.runType = runType;

        if (runType == RunType.DEBUG) {
            command = "pydev_proxy";
        } else {
            command = "run";
        }

        String pathSep = System.getProperty("path.separator");

        this.runConf.setInterpreterOptions(String.format("-m %s %s", Const.get().packageName, command));

        this.session.start();

        // Set Envs
        this.runConf.getEnvs().put(this.IDE_NAME_ENV, ApplicationInfo.getInstance().getFullApplicationName());
        this.runConf.getEnvs().put(this.IDE_PLUGIN_VERSION_ENV, Const.get().version);
        this.runConf.getEnvs().put(this.IDE_VERSION_ENV, ApplicationInfo.getInstance().getFullVersion());
        this.runConf.getEnvs().put(this.IDE_TYPE_ENV, Const.get().pycharm);

        ProjectState state = ProjectSettings.getInstance(this.runConf.getProject()).getState();

        this.runConf.getEnvs().put(this.DEBUGGER_SPEEDUPS_ENV, EnvUtils.boolToEnv(state.debuggerSpeedups));
        this.runConf.getEnvs().put(this.VERBOSE_ENV, EnvUtils.boolToEnv(state.verbose));
        this.runConf.getEnvs().put(this.CACHE_ENV, EnvUtils.boolToEnv(state.cache));
        this.runConf.getEnvs().put(this.PRINT_LOGO_ENV, EnvUtils.boolToEnv(state.printLogo));
        this.runConf.getEnvs().put(this.WATCHCWD_ENV, EnvUtils.boolToEnv(state.watchCwd));
        this.runConf.getEnvs().put(this.IDE_SERVER_PORT_ENV, String.valueOf(this.session.getPort()));
        this.runConf.getEnvs().put(this.TIME_PROFILE_ENV, EnvUtils.boolToEnv(state.profile));
        this.runConf.getEnvs().put("PYDEVD_USE_CYTHON", "NO");
        this.runConf.getEnvs().put(this.TELEMETRY_ENV, EnvUtils.boolToEnv(preferences.telemetry));
        this.runConf.getEnvs().put(this.SENTRY_ENV, EnvUtils.boolToEnv(preferences.sentry));

        Gson gson = new Gson();
        String quickConfigPayload = gson.toJson(QuickConfigStateFactory.create());
        this.runConf.getEnvs().put(this.QUICK_CONFIG_ENV, quickConfigPayload);

        List<String> reloadiumPath = new ArrayList<>(state.reloadiumPath);
        if (state.watchSourceRoots) {
            @Nullable Module module = this.runConf.getModule();
            if (module != null) {
                VirtualFile[] sourceRootsRaw = ModuleRootManager.getInstance(module).getSourceRoots(false);

                for (VirtualFile f : sourceRootsRaw) {
                    if (TempFileSystem.class.isAssignableFrom(f.getFileSystem().getClass())) {
                        continue;
                    }
                    reloadiumPath.add(f.toNioPath().toString());
                }
            }
        }

        if (reloadiumPath.isEmpty() && this.runConf.getWorkingDirectory().isEmpty()) {
            reloadiumPath.add(this.project.getBasePath());
        }

        List<String> reloadiumPathRemote = this.getRemotePaths(reloadiumPath);

        this.runConf.getEnvs().put(this.RELOADIUMPATH_ENV, String.join(pathSep, reloadiumPathRemote));

        List<String> reloadiumIgnore = new ArrayList<>(state.reloadiumIgnore);
        List<String> reloadiumIgnoreRemote = this.getRemotePaths(reloadiumIgnore);
        this.runConf.getEnvs().put(this.RELOADIUMIGNORE_ENV, String.join(pathSep, reloadiumIgnoreRemote));

        // Set PYTHONPATH
        String pythonpath = this.runConf.getEnvs().getOrDefault("PYTHONPATH", "");

        if(!pythonpath.isBlank()) {
            pythonpath = String.format("%s%s%s", pythonpath, pathSep, System.getenv("PYTHONPATH"));
        }
        else {
            pythonpath = System.getenv("PYTHONPATH");

            if (pythonpath == null) {
                pythonpath = "";
            }
        }

        assert this.sdkHandler != null;
        String packagePath = this.sdkHandler.getPackageDir().toString();

        if (!pythonpath.isBlank()) {
            pythonpath = String.format("%s%s%s", packagePath, pathSep, pythonpath);
        } else {
            pythonpath = packagePath;
        }
        this.runConf.getEnvs().put("PYTHONPATH", pythonpath);  //  # RwRender: this.runConf.getEnvs().put("PYTHONPATH", {{ ctx.pythonpath }});  //
    }

    private List<String> getRemotePaths(List<String> paths) {
        List<String> ret = new ArrayList<>();

        for (String p : paths) {
            try {
                ret.add(this.convertPathToRemote(p));
            } catch (Exception e) {
                RwSentry.get().captureException(e);
            }
        }
        return ret;
    }

    @Override
    public void onProcessExit() {
        super.onProcessExit();
        this.session.close();
        IconPatcher.refresh(this.project);
    }

    @Override
    public void afterRun() {
        this.runConf.setInterpreterOptions(this.origRunConf.getInterpreterOptions());
        runConf.setEnvs(this.origRunConf.getEnvs());
    }

    public boolean isReloadiumActivated() {
        boolean ret = this.runConf.getInterpreterOptions().contains("reloadium");
        return ret;
    }
}
