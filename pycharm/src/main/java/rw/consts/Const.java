package rw.consts;


import com.intellij.ui.JBColor;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.VisibleForTesting;
import rw.util.Architecture;
import rw.util.OsType;

import java.awt.*;
import java.io.File;

public final class Const {
    @VisibleForTesting
    public static Const singleton = null;

    public final String packageName = "reloadium";  //  # RwRender: public final String packageName = "{{ ctx.package_name }}";  //
    public final String version = "1.0.1";  //  # RwRender: public final String version = "{{ ctx.version }}";  //
    public final String sentryDsn = "http://cd9eecceaa4849d6947b3446ac038c2d@sentry.reloadware.local/5";  //  # RwRender: public final String sentryDsn = "{{ ctx.sentry_dsn }}";  //
    public Stage stage = Stage.LOCAL;  //  # RwRender: public Stage stage = Stage.{{ ctx.stage.upper() }};  //
    public final String packageDirName = "package_local";  //  # RwRender: public final String packageDirName = "{{ ctx.package_dir_name }}";  //

    public Msgs msgs = new Msgs(packageName);
    public final String configFilename = "config-local.json";  //  # RwRender: public final String configFilename = "{{ ctx.package.db.filename }}";  //
    public final String legalUrl = "https://reloadium.io/legal/";
    public final String privacyPolicyUrl = "https://reloadium.io/privacy-policy/";

    public final String launcherName = "reloadium_launcher";

    public final String pycharm = "PyCharm";  //  # RwRender: public final String pycharm = "{{ ctx.ide_type.pycharm }}";  //

    public final Color brandColor = new JBColor(new Color(255, 114, 0), new Color(255, 114, 0));

    @VisibleForTesting
    public Const() {
    }

    public static Const get()
    {
        if (singleton == null)
            singleton = new Const();

        return singleton;
    }
}
