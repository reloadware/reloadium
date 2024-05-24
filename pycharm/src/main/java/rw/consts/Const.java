package rw.consts;


import com.intellij.ui.JBColor;
import org.jetbrains.annotations.VisibleForTesting;

import java.awt.*;
import java.net.URI;

public final class Const {
    @VisibleForTesting
    public static Const singleton = null;

    public final String packageName = "reloadium";  //  # RwRender: public final String packageName = "{{ ctx.package_name }}";  //
    public final String version = "1.5.1";  //  # RwRender: public final String version = "{{ ctx.version }}";  //
    public final String sentryDsn = "http://cd9eecceaa4849d6947b3446ac038c2d@sentry.reloadware.local/5";  //  # RwRender: public final String sentryDsn = "{{ ctx.sentry_dsn }}";  //
    public final String packageDirName = "package_local";  //  # RwRender: public final String packageDirName = "{{ ctx.package_dir_name }}";  //
    public final String configFilename = "config-local.json";  //  # RwRender: public final String configFilename = "{{ ctx.package.db.filename }}";  //
    public final URI havenFrontendAddress = URI.create("http://reloadium.reloadware.local/");  //  # RwRender: public final URI havenFrontendAddress = URI.create("{{ ctx.haven_frontend.address }}/");  //
    public final URI legalUrl = havenFrontendAddress.resolve("legal/");
    public final URI privacyPolicyUrl = havenFrontendAddress.resolve("privacy-policy/");
    public final URI pricingUrl = havenFrontendAddress.resolve("pricing/");
    public final String launcherName = "reloadium_launcher";
    public final String pycharm = "PyCharm";  //  # RwRender: public final String pycharm = "{{ ctx.ide_type.pycharm }}";  //
    public final Color brandColor = new JBColor(new Color(255, 114, 0), new Color(255, 114, 0));
    public Stage stage = Stage.LOCAL;  //  # RwRender: public Stage stage = Stage.{{ ctx.stage.upper() }};  //
    public Msgs msgs = new Msgs(packageName);

    @VisibleForTesting
    public Const() {
    }

    public static Const get() {
        if (singleton == null)
            singleton = new Const();

        return singleton;
    }
}
