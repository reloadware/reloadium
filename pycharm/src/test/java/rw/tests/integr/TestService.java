package rw.tests.integr;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rw.tests.BaseTestCase;
import rw.tests.fixtures.PackageFixture;
import rw.tests.utils.MiscUtils;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class TestService extends BaseTestCase {

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testBuiltinInstalledOnStart() throws Exception {
        this.service.init();
        MiscUtils.assertInstalled(this.packageManager, this.builtinVersion);
    }

    @Test
    public void testInstallingOnMissing() throws Exception {
        this.service.init();
        PackageFixture packageFixture = new PackageFixture(this.packageManager, this.webVersion.toString());

        FileUtils.deleteDirectory(this.packageManager.getFs().getPackagesRootDir());

        this.service.checkIfStillGood();

        verify(this.service.packageManager,
                times(1)).install();

        MiscUtils.assertInstalled(this.packageManager, this.webVersion);
    }
}
