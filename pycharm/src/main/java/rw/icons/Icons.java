package rw.icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/*
 * https://www.jetbrains.org/intellij/sdk/docs/reference_guide/work_with_icons_and_images.html
 */
public interface Icons {
    Icon Run = IconLoader.getIcon("/icons/runWithReloadium.svg", Icons.class);
    Icon Restart = IconLoader.getIcon("/icons/restartWithReloadium.svg", Icons.class);
    Icon Debug = IconLoader.getIcon("/icons/debugWithReloadium.svg", Icons.class);
    Icon RestartDebugger = IconLoader.getIcon("/icons/restartDebuggerWithReloadium.svg", Icons.class);
    Icon Update = IconLoader.getIcon("/icons/updateReloadium.svg", Icons.class);
    Icon Reloadable = IconLoader.getIcon("/icons/reloadable.svg", Icons.class);
    Icon ProductIcon = IconLoader.getIcon("/icons/reloadiumIcon.svg", Icons.class);
    Icon Settings = IconLoader.getIcon("/icons/settings.svg", Icons.class);
    Icon About = IconLoader.getIcon("/icons/about.svg", Icons.class);
    Icon AboutLogo = IconLoader.getIcon("/icons/aboutLogo.svg", Icons.class);
    Icon Timing = IconLoader.getIcon("/icons/itiming.svg", Icons.class);
}
