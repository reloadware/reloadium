package rw.tests.integr;

import org.junit.jupiter.api.*;
import rw.tests.BaseMockedTestCase;
import rw.tests.utils.MiscUtils;
import rw.tests.fixtures.PackageFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;


public class TestDownloading extends BaseMockedTestCase {

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testInstall() throws Exception {
        this.webPackageManager.install(null);

        MiscUtils.assertInstalled(this.webVersion);
    }

    @Test
    public void testUpdate() throws Exception {
        PackageFixture packageFixture = new PackageFixture("0.2.1");
        assertThat(this.webPackageManager.getCurrentVersion()).isEqualTo("0.2.1");
        this.webPackageManager.install( null);

        MiscUtils.assertInstalled(this.webVersion);
    }
}
