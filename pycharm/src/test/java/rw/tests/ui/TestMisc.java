package rw.tests.ui;


import com.automation.remarks.junit5.Video;
import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.fixtures.ContainerFixture;
import com.intellij.remoterobot.utils.Keyboard;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import rw.tests.Depot;
import rw.tests.Package;
import rw.tests.ui.fixtures.Root;
import rw.tests.ui.steps.MiscSteps;
import rw.tests.ui.utils.RemoteRobotExtension;
import rw.tests.utils.MiscUtils;

import java.nio.file.Path;

import static com.intellij.remoterobot.utils.RepeatUtilsKt.waitFor;
import static java.time.Duration.ofSeconds;


@ExtendWith(RemoteRobotExtension.class)
public class TestMisc extends PackageTestBase {
    private final MiscSteps steps = new MiscSteps(remoteRobot);
    private final Keyboard keyboard = new Keyboard(remoteRobot);
    private final Path testProjects = Path.of("src/test/testProjects").toAbsolutePath();

    @Test
    @Order(1)
    @Video
    void installsPackage(final RemoteRobot remoteRobot) throws Exception {
        Depot.run("env.get_remote().clean()");
        Package.run("p.rm_config");
        Package.run("p.push_release");
        String version = Package.run("v.current_version")[0];
        Depot.run("p.publish_release");

        try {
            steps.createNewProject();
        } catch (Exception exception) {
            MiscUtils.sleep(15.0f);
            steps.createNewProject();
        }

        final Root root = remoteRobot.find(Root.class, ofSeconds(5));

        root.runWithReloadium().click();

        MiscUtils.sleep(2.0f);

        root.firstRunDialog().okButton().click();

        ContainerFixture console = root.console();
        waitFor(ofSeconds(10), () -> console.hasText(String.format("Reloadium %s", version)));
        waitFor(ofSeconds(10), () -> console.hasText("Hi, PyCharm"));
        waitFor(ofSeconds(10), () -> console.hasText("Process finished with exit code 0"));
    }
}