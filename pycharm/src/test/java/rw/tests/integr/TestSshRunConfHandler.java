package rw.tests.integr;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import rw.action.RunType;
import rw.action.RunWithReloadium;
import rw.handler.SshRunConfHandler;
import rw.tests.BaseTestCase;
import rw.tests.fixtures.CakeshopFixture;
import rw.tests.fixtures.PackageFixture;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;


@ExtendWith(MockitoExtension.class)
public class TestSshRunConfHandler extends BaseTestCase {
    CakeshopFixture cakeshop;
    AnAction action;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        PackageFixture packageFixture = new PackageFixture(this.packageManager, "0.7.12");
        this.cakeshop = new CakeshopFixture(this.getProject());
        this.cakeshop.setUp();

        this.action = ActionManager.getInstance().getAction(RunWithReloadium.ID);

    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.cakeshop.tearDown();

        super.tearDown();
    }

    @Test
    public void testSetsUserId() {
        String randomUuidValue = "8fcb78db-229d-40ee-b4ef-86d415755ec0";
        UUID randomUuid = UUID.fromString(randomUuidValue);

        try (MockedStatic<UUID> uuid = mockStatic(UUID.class)) {
            uuid.when(UUID::randomUUID).thenReturn(randomUuid);

            SshRunConfHandler remoteRunConfHandler = new SshRunConfHandler(this.cakeshop.getRunConf());

            remoteRunConfHandler.beforeRun(RunType.RUN);

            String userId = this.cakeshop.getRunConf().getEnvs().get("RW_USERID");
            assertThat(userId.equals(randomUuidValue)).isTrue();
        }
    }

    @Test
    public void testCreatesConfig() throws IOException {
        UUID randomUuid = UUID.fromString("8fcb78db-229d-40ee-b4ef-86d415755ec0");

        assertThat(!this.packageManager.getFs().getConfigFile().exists());

        try (MockedStatic<UUID> uuid = mockStatic(UUID.class)) {
            uuid.when(UUID::randomUUID).thenReturn(randomUuid);
            SshRunConfHandler remoteRunConfHandler = new SshRunConfHandler(this.cakeshop.getRunConf());

            remoteRunConfHandler.beforeRun(RunType.RUN);

            assertThat(this.packageManager.getFs().getConfigFile().exists());

            assertThat(FileUtils.readFileToString(this.packageManager.getFs().getConfigFile(), "utf-8")).isEqualTo(
                    "{\n" +
                            "  \"user\": {\n" +
                            "    \"uuid\": \"8fcb78db-229d-40ee-b4ef-86d415755ec0\"\n" +
                            "  }\n" +
                            "}"
            );
        }
    }

    @Test
    public void testAlreadyExists() throws IOException {
        UUID randomUuid = UUID.fromString("8fcb78db-229d-40ee-b4ef-86d415755ec0");

        String content = "{\n" +
                "  \"user\": {\n" +
                "    \"uuid\": \"1fcb78db-229d-30ee-b4ef-16d415755ec0\"\n" +
                "  }\n" +
                "}";

        FileUtils.writeStringToFile(this.packageManager.getFs().getConfigFile(), content, "utf-8");

        try (MockedStatic<UUID> uuid = mockStatic(UUID.class)) {
            uuid.when(UUID::randomUUID).thenReturn(randomUuid);
            SshRunConfHandler remoteRunConfHandler = new SshRunConfHandler(this.cakeshop.getRunConf());

            remoteRunConfHandler.beforeRun(RunType.RUN);

            assertThat(this.packageManager.getFs().getConfigFile().exists());
            assertThat(FileUtils.readFileToString(this.packageManager.getFs().getConfigFile(), "utf-8")).isEqualTo(content);
        }
    }
}
