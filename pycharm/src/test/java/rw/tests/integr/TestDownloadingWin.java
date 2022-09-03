package rw.tests.integr;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rw.tests.BaseMockedTestCase;
import rw.tests.fixtures.OsFixture;
import rw.tests.utils.MiscUtils;
import rw.util.OsType;

import static org.assertj.core.api.Assertions.assertThat;


public class TestDownloadingWin extends BaseMockedTestCase {
    OsFixture osFixture;

    @BeforeEach
    protected void setUp() throws Exception {
        this.osFixture = new OsFixture(OsType.Windows);
        this.osFixture.start();

        super.setUp();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.osFixture.stop();
        super.tearDown();
    }

    @Test
    public void testInstall() throws Exception {
        this.webPackageManager.install(null);

        MiscUtils.assertInstalled(this.webVersion);
    }
}
