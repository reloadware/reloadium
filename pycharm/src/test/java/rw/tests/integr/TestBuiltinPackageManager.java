package rw.tests.integr;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rw.tests.BaseTestCase;
import rw.tests.fixtures.LinuxFixture;
import rw.tests.fixtures.M1Fixture;
import rw.tests.fixtures.PackageFixture;
import rw.tests.utils.MiscUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


public class TestBuiltinPackageManager extends BaseTestCase {
    LinuxFixture linuxFixture;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        this.linuxFixture = new LinuxFixture();
        this.linuxFixture.start();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.linuxFixture.stop();
        super.tearDown();
    }

    @Test
    public void testGetWheelFiles() throws Exception {
        List<String> filenames = this.packageManager.getWheels().stream().map(f -> f.getFilename()).collect(Collectors.toList());

        assertThat(filenames).isEqualTo(List.of(
                "reloadium-0.7.13-cp37-cp37m-manylinux_2_17_x86_64.manylinux2014_x86_64.whl",
                "reloadium-0.7.13-cp38-cp38-manylinux_2_17_x86_64.manylinux2014_x86_64.whl",
                "reloadium-0.7.13-cp39-cp39-manylinux_2_17_x86_64.manylinux2014_x86_64.whl",
                "reloadium-0.7.13-cp310-cp310-manylinux_2_17_x86_64.manylinux2014_x86_64.whl"
        ));
    }

    @Test
    public void testDirNames() throws Exception {
        List<String> filenames = this.packageManager.getWheels().stream().map(f -> f.getDstDirName()).collect(Collectors.toList());

        assertThat(filenames).isEqualTo(List.of(
                "3.7", "3.8", "3.9", "3.10"
        ));
    }

    @Test
    public void testShouldInstallAlreadyInstalled() throws Exception {
        PackageFixture packageFixture = new PackageFixture(this.packageManager, this.packageManager.getBuiltinVersion());
        assertThat(this.packageManager.shouldInstall()).isFalse();
    }

    @Test
    public void testShouldInstallCurentVersionOld() throws Exception {
        PackageFixture packageFixture = new PackageFixture(this.packageManager, "0.0.0");
        assertThat(this.packageManager.shouldInstall()).isTrue();
    }

    @Test
    public void testShouldInstallCurentVersionNewer() throws Exception {
        PackageFixture packageFixture = new PackageFixture(this.packageManager, "1000.0.0");
        assertThat(this.packageManager.shouldInstall()).isTrue();
    }

    @Test
    public void testInstalling() throws Exception {
        this.packageManager.install();
        MiscUtils.assertInstalled(this.packageManager, this.builtinVersion);
    }

    @Test
    public void testDowngrading() throws Exception {
        PackageFixture packageFixture = new PackageFixture(this.packageManager, "1000.0.0");
        this.packageManager.install();
        MiscUtils.assertInstalled(this.packageManager, this.builtinVersion);
    }
}
