package rw.tests.ui.fixtures;

import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.data.RemoteComponent;
import com.intellij.remoterobot.fixtures.*;
import org.jetbrains.annotations.NotNull;
import rw.config.Config;
import rw.tests.utils.MiscUtils;

import java.time.Duration;

import static com.intellij.remoterobot.search.locators.Locators.byXpath;
import static com.intellij.remoterobot.utils.RepeatUtilsKt.waitFor;
import static java.time.Duration.ofSeconds;

@FixtureName(name = "Root")
@DefaultXpath(by = "IdeFrameImpl type", xpath = "//div[@class='IdeFrameImpl']")
public class Root extends IdeaFrame {
    public Root(@NotNull RemoteRobot remoteRobot, @NotNull RemoteComponent remoteComponent) {
        super(remoteRobot, remoteComponent);
    }

    public IdeStatusBarFixture ideStatusBar() {
        return this.find(IdeStatusBarFixture.class);
    }

    public ActionButtonFixture runWithReloadium() {
        return this.find(ActionButtonFixture.class, byXpath("//div[@myicon='runWithReloadium.svg']"), ofSeconds(120));
    }

    public ActionButtonFixture debugWithReloadium() {
        return this.find(ActionButtonFixture.class, byXpath("//div[@myicon='debugWithReloadium.svg']"), ofSeconds(120));
    }

    public ContainerFixture console() throws InterruptedException {
        ContainerFixture ret = this.find(ContainerFixture.class, byXpath("//div[@class='ConsoleViewImpl']"), ofSeconds(10));
        MiscUtils.sleep(1.0f);
        return ret;
    }

    public void assertInstallNotification() {
        this.find(ComponentFixture.class,
                byXpath(String.format("//div[@accessiblename='%s']", Config.get().msgs.UPDATED_SUCCESSFULLY)),
                Duration.ofSeconds(60));
    }

    public void assertInstallErrorNotification() {
        this.find(ComponentFixture.class,
                byXpath(String.format("//div[@accessiblename='%s']", Config.get().msgs.INSTALLING_FAILED)),
                Duration.ofSeconds(60));
    }

    public void assertButtonsEnabled() {
        MiscUtils.sleep(5.0f);

        ActionButtonFixture runWithReloadium = this.runWithReloadium();
        ActionButtonFixture debugWithReloadium = this.debugWithReloadium();

        waitFor(ofSeconds(60), runWithReloadium::isEnabled);
        waitFor(ofSeconds(60), debugWithReloadium::isEnabled);

        MiscUtils.sleep(15.0f);

        waitFor(ofSeconds(60), runWithReloadium::isEnabled);
        waitFor(ofSeconds(60), debugWithReloadium::isEnabled);
    }

    public void assertButtonsDisabled() {
        ActionButtonFixture runWithReloadium = this.runWithReloadium();
        ActionButtonFixture debugWithReloadium = this.debugWithReloadium();

        MiscUtils.sleep(2.0f);

        assert !runWithReloadium.isEnabled();
        assert !debugWithReloadium.isEnabled();
    }
}
