package rw.tests.ui.fixtures;

import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.data.RemoteComponent;
import com.intellij.remoterobot.fixtures.*;
import org.jetbrains.annotations.NotNull;

import static com.intellij.remoterobot.search.locators.Locators.byXpath;

@DefaultXpath(by = "IdeStatusBarImpl type", xpath = "//div[@class='IdeStatusBarImpl']")
@FixtureName(name = "Ide Status Bar")
public class IdeStatusBarFixture extends CommonContainerFixture {
    public IdeStatusBarFixture(@NotNull RemoteRobot remoteRobot, @NotNull RemoteComponent remoteComponent) {
        super(remoteRobot, remoteComponent);
    }

    public ComponentFixture inlineProgressPanel() {
        return find(ContainerFixture.class, byXpath("//div[@class='InlineProgressPanel']"));
    }

    public ComponentFixture ideErrorsIcon() {
        return find(ComponentFixture.class, byXpath("//div[@class='IdeErrorsIcon']"));
    }

    public ComponentFixture withIconAndArrows(String accessiblename) {
        return find(ComponentFixture.class, byXpath("//div[@accessiblename='" + accessiblename + "' and @class='WithIconAndArrows']"));
    }
}