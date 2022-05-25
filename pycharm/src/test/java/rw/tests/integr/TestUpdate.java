package rw.tests.integr;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.testFramework.TestActionEvent;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import rw.action.Update;
import rw.consts.Const;
import rw.tests.BaseMockedTestCase;
import rw.tests.fixtures.PackageFixture;
import rw.tests.fixtures.NotificationManagerFixture;
import rw.tests.utils.MiscUtils;
import rw.util.NotificationManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestUpdate extends BaseMockedTestCase {
    AnAction action;
    public NotificationManagerFixture notificationManagerFixture;
    public NotificationManager notificationManager;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        this.action = ActionManager.getInstance().getAction(Update.ID);
        this.notificationManagerFixture = new NotificationManagerFixture();
        this.notificationManagerFixture.start();
        this.notificationManager = this.notificationManagerFixture.getNotificationManager();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.notificationManagerFixture.stop();
        super.tearDown();
    }

    @Test
    public void testInstallingWhenNotInstalled() throws Exception {
        FileUtils.deleteDirectory(Const.get().getPackagesRootDir());

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();

        this.action.actionPerformed(event);

        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();

        MiscUtils.assertInstalled(this.webVersion);

        verify(this.webPackageManager, times(1)).install(any());
        verify(this.notificationManager, times(1)).show(any(),
                eq(Const.get().msgs.UPDATED_SUCCESSFULLY),
                eq(""),
                eq(NotificationType.INFORMATION));
    }

    @Test
    public void testAlreadyUpToDate() throws Exception {
        PackageFixture packageFixture = new PackageFixture(this.webVersion.toString());

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();

        this.action.actionPerformed(event);

        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();

        verify(this.webPackageManager, times(0)).install(any());
        verify(this.notificationManager, times(1)).show(any(),
                eq(Const.get().msgs.ALREADY_UP_TO_DATE),
                eq(""),
                eq(NotificationType.INFORMATION));

        MiscUtils.assertInstalled(this.webVersion);
    }

    @Test
    public void testUpdate() throws Exception {
        PackageFixture packageFixture = new PackageFixture("0.0.0");

        FileUtils.deleteDirectory(Const.get().getPackagesRootDir());

        AnActionEvent event = new TestActionEvent();
        this.action.update(event);
        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();

        this.action.actionPerformed(event);

        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();

        verify(this.webPackageManager, times(1)).install(any());
        verify(this.notificationManager, times(1)).show(any(),
                eq(Const.get().msgs.UPDATED_SUCCESSFULLY),
                eq(""),
                eq(NotificationType.INFORMATION));

        MiscUtils.assertInstalled(this.webVersion);
        assertThat(packageFixture.packageDistInfoDir.exists()).isFalse();
    }
}
