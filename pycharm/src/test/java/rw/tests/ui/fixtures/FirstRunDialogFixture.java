// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package rw.tests.ui.fixtures;

import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.data.RemoteComponent;
import com.intellij.remoterobot.fixtures.CommonContainerFixture;
import com.intellij.remoterobot.fixtures.DefaultXpath;
import com.intellij.remoterobot.fixtures.FixtureName;
import com.intellij.remoterobot.fixtures.JButtonFixture;
import org.jetbrains.annotations.NotNull;

import static com.intellij.remoterobot.search.locators.Locators.byXpath;


@DefaultXpath(by = "MyDialog type", xpath = "//*[@title='Using Reloadium in run mode']")
@FixtureName(name = "FirstRunFixture")
public class FirstRunDialogFixture extends CommonContainerFixture {
    public FirstRunDialogFixture(@NotNull RemoteRobot remoteRobot, @NotNull RemoteComponent remoteComponent) {
        super(remoteRobot, remoteComponent);
    }

    public JButtonFixture okButton() {
        return this.find(
                JButtonFixture.class,
                byXpath("//div[@text='OK']")
        );
    }
}
