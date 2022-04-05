// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package rw.action;

import com.intellij.ide.AboutPopupDescriptionProvider;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.actions.AboutPopup;
import com.intellij.ide.nls.NlsMessages;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.application.ex.ApplicationInfoEx;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.AppUIUtil;
import com.intellij.ui.HyperlinkAdapter;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.LicensingFacade;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.scale.ScaleContext;
import com.intellij.util.ObjectUtils;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.config.Config;
import rw.service.Service;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

import static rw.icons.Icons.AboutLogo;

/**
 * @author Konstantin Bulenkov
 */
public class AboutDialog extends DialogWrapper {
    public AboutDialog(@Nullable Project project) {
        super(project, false);
        setResizable(false);
        setTitle(String.format("About %s", StringUtil.capitalize(Config.get().packageName)));

        init();
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
        String appName = StringUtil.capitalize(Config.get().packageName);

        box.add(label(appName, JBFont.label().biggerOn(3).asBold()));
        box.add(Box.createVerticalStrut(10));

        String pluginInfo = String.format("Plugin version: %s", Config.get().version);
        String packageInfo = String.format("Package version: %s", service.webPackageManager.getCurrentVersion());

        box.add(label(pluginInfo, getDefaultTextFont()));
        box.add(label(packageInfo, getDefaultTextFont()));
        addEmptyLine(box);

        //Copyright
        box.add(label(String.format("Copyright © %d %s", Calendar.getInstance(Locale.US).get(Calendar.YEAR), "Reloadware"),
                getDefaultTextFont()));
        addEmptyLine(box);

        return box;
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
}
