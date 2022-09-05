package rw.tests.integr;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rw.tests.BaseMockedTestCase;
import rw.tests.fixtures.PackageFixture;
import rw.tests.utils.MiscUtils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;


public class TestBuiltinPackageManager extends BaseMockedTestCase {
    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

    }

    @AfterEach
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testGetWheelFiles() throws Exception {
        List<String> filenames = this.builtinPackageManager.getWheelFiles().stream().map(f -> f.getName()).collect(Collectors.toList());

        assertThat(filenames).isEqualTo(List.of(
                "reloadium-0.7.13-cp37-cp37m-manylinux_2_17_x86_64.manylinux2014_x86_64.whl",
                "reloadium-0.7.13-cp38-cp38-manylinux_2_17_x86_64.manylinux2014_x86_64.whl",
                "reloadium-0.7.13-cp39-cp39-manylinux_2_17_x86_64.manylinux2014_x86_64.whl",
                "reloadium-0.7.13-cp310-cp310-manylinux_2_17_x86_64.manylinux2014_x86_64.whl"
        ));
    }

    @Test
    public void testShouldInstallAlreadyInstalled() throws Exception {
        PackageFixture packageFixture = new PackageFixture(this.builtinPackageManager.getBuiltinVersion());
        assertThat(this.builtinPackageManager.shouldInstall()).isFalse();
    }

    @Test
    public void testShouldInstallCurentVersionOld() throws Exception {
        PackageFixture packageFixture = new PackageFixture("0.0.0");
        assertThat(this.builtinPackageManager.shouldInstall()).isTrue();
    }

    @Test
    public void testShouldInstallCurentVersionNewer() throws Exception {
        PackageFixture packageFixture = new PackageFixture("1000.0.0");
        assertThat(this.builtinPackageManager.shouldInstall()).isFalse();
    }

    @Test
    public void testInstalling() throws Exception {
        this.builtinPackageManager.install(null);
        MiscUtils.assertInstalled(this.builtinVersion);
    }
}