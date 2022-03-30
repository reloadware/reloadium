package rw.tests.unit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rw.config.Config;
import rw.tests.BaseMockedTestCase;
import rw.tests.fixtures.WinFixture;
import rw.tests.utils.MiscUtils;

import static org.assertj.core.api.Assertions.assertThat;


public class TestDownloadingWin extends BaseMockedTestCase {
    WinFixture winFixture;

    @BeforeEach
    protected void setUp() throws Exception {
        this.winFixture = new WinFixture();
        this.winFixture.start();

        super.setUp();

        if (Config.get().getPackagePythonVersionDir("3.9").exists()) {
            int a = 1;
        }
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.winFixture.stop();
        super.tearDown();
    }

    @Test
    public void testInstall() throws Exception {
        this.webPackageManager.install(null);

        MiscUtils.assertInstalled(this.webVersion);
    }
}
