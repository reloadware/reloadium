package rw.tests.ui;

import com.intellij.remoterobot.RemoteRobot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import rw.tests.ui.utils.StepsLogger;
import rw.tests.utils.MiscUtils;

import static com.intellij.remoterobot.stepsProcessing.StepWorkerKt.step;
import static rw.tests.ui.fixtures.ActionMenuFixtureKt.actionMenu;
import static rw.tests.ui.fixtures.ActionMenuFixtureKt.actionMenuItem;


public class PackageTestBase {
    protected final RemoteRobot remoteRobot = new RemoteRobot("http://127.0.0.1:8082");

    @BeforeAll
    public static void initLogging() {
        StepsLogger.init();
    }

    @AfterEach
    public void closeProject(final RemoteRobot remoteRobot) {
        MiscUtils.sleep(1.0f);

        step("Close the project", () -> {
            actionMenu(this.remoteRobot, "File").click();
            actionMenuItem(this.remoteRobot, "Close Project").click();
        });
    }
}