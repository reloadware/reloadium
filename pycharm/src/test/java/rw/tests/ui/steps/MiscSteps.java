// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package rw.tests.ui.steps;

import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.utils.Keyboard;
import rw.tests.ui.fixtures.NewProjectFixture;
import rw.tests.ui.fixtures.WelcomeFrameFixture;
import rw.tests.utils.MiscUtils;

import static com.intellij.remoterobot.stepsProcessing.StepWorkerKt.step;
import static java.time.Duration.ofSeconds;


public class MiscSteps {
    final private RemoteRobot remoteRobot;
    final private Keyboard keyboard;

    public MiscSteps(RemoteRobot remoteRobot) {
        this.remoteRobot = remoteRobot;
        this.keyboard = new Keyboard(remoteRobot);
    }

    public void createNewProject() {
        step("Create New Python Project", () -> {
            final WelcomeFrameFixture welcomeFrame = remoteRobot.find(WelcomeFrameFixture.class, ofSeconds(60));
            welcomeFrame.createNewProjectButton().click();

            MiscUtils.sleep(2.0f);

            final NewProjectFixture newProjectDialog = remoteRobot.find(NewProjectFixture.class, ofSeconds(20));
            newProjectDialog.createButton().click();
        });
    }
}
