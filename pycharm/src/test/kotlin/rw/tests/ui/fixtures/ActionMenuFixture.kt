// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package rw.tests.ui.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.ComponentFixture
import com.intellij.remoterobot.fixtures.FixtureName
import com.intellij.remoterobot.search.locators.byXpath
import com.intellij.remoterobot.utils.waitFor

fun RemoteRobot.actionMenu(text: String): ActionMenuFixture {
    val xpath = byXpath("text '$text'", "//div[@class='ActionMenu' and @text='$text']")
    waitFor {
        findAll<ActionMenuFixture>(xpath).isNotEmpty()
    }
    return findAll<ActionMenuFixture>(xpath).first()
}

fun RemoteRobot.actionMenuItem(text: String): ActionMenuItemFixture {
    val xpath = byXpath("text '$text'", "//div[@class='ActionMenuItem' and @text='$text']")
    waitFor {
        findAll<ActionMenuItemFixture>(xpath).isNotEmpty()
    }
    return findAll<ActionMenuItemFixture>(xpath).first()
}

@FixtureName("ActionMenu")
class ActionMenuFixture(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) :
    ComponentFixture(remoteRobot, remoteComponent)

@FixtureName("ActionMenuItem")
class ActionMenuItemFixture(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) :
    ComponentFixture(remoteRobot, remoteComponent)