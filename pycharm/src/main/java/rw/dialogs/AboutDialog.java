package rw.dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;
import rw.config.Config;
import rw.config.ConfigManager;
import rw.consts.Const;
import rw.service.Service;

import javax.swing.*;
import java.util.Calendar;
import java.util.Locale;

import static rw.icons.Icons.AboutLogo;


public class AboutDialog extends DialogWrapper {
    public AboutDialog(@Nullable Project project) {
        super(project, false);
        this.setResizable(false);
        this.setTitle(String.format("About %s", StringUtil.capitalize(Const.get().packageName)));

        this.init();
    }

    private static JBFont getDefaultTextFont() {
        return JBFont.label().lessOn(1);
    }

    private static void addEmptyLine(Box box) {
        box.add(Box.createVerticalStrut(18));
    }

    private static JLabel label(@NlsContexts.Label String text, JBFont font) {
        JBLabel label = new JBLabel(text).withFont(font);
        label.setCopyable(true);
        label.setAllowAutoWrapping(true);
        return label;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        Box box = getText();
        JLabel icon = new JLabel(AboutLogo);
        icon.setVerticalAlignment(SwingConstants.TOP);
        icon.setBorder(JBUI.Borders.empty(20, 12, 0, 24));
        box.setBorder(JBUI.Borders.empty(20, 0, 0, 20));

        return JBUI.Panels.simplePanel()
                .addToLeft(icon)
                .addToCenter(box);
    }

    private Box getText() {
        Service service = Service.get();
        Box box = Box.createVerticalBox();

        String license;
        try(Config config = ConfigManager.get().getConfig(true)) {
            license = config.account.lastLicenseType;
        }

        if(license != null && license.length() > 2) {
            license = Character.toUpperCase(license.charAt(0)) + license.substring(1).toLowerCase();
        }

        String appName = StringUtil.capitalize(Const.get().packageName + " " + license);

        box.add(label(appName, JBFont.label().biggerOn(3).asBold()));
        box.add(Box.createVerticalStrut(10));

        String pluginInfo = String.format("Plugin version: %s", Const.get().version);
        String packageInfo = String.format("Package version: %s", service.getPackageManager().getCurrentVersion());

        box.add(label(pluginInfo, getDefaultTextFont()));
        box.add(label(packageInfo, getDefaultTextFont()));
        addEmptyLine(box);

        //Copyright
        box.add(label(String.format("Copyright © %d %s", Calendar.getInstance(Locale.US).get(Calendar.YEAR), "Reloadware"),
                getDefaultTextFont()));
        addEmptyLine(box);

        return box;
    }
}
