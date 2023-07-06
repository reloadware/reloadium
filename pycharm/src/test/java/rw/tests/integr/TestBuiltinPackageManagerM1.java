package rw.tests.integr;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rw.tests.BaseTestCase;
import rw.tests.fixtures.M1Fixture;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


public class TestBuiltinPackageManagerM1 extends BaseTestCase {
    M1Fixture m1Fixture;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        this.m1Fixture = new M1Fixture();
        this.m1Fixture.start();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.m1Fixture.stop();
        super.tearDown();
    }

    @Test
    public void testGetWheelFiles() throws Exception {
        List<String> filenames = this.packageManager.getWheels().stream().map(f -> f.getFilename()).collect(Collectors.toList());

        assertThat(filenames).isEqualTo(List.of(
                "reloadium-0.7.13-cp37-cp37m-macosx_10_15_x86_64.whl",
                "reloadium-0.7.13-cp38-cp38-macosx_11_0_arm64.whl",
                "reloadium-0.7.13-cp38-cp38-macosx_10_15_x86_64.whl",
                "reloadium-0.7.13-cp39-cp39-macosx_11_0_arm64.whl",
                "reloadium-0.7.13-cp39-cp39-macosx_10_15_x86_64.whl",
                "reloadium-0.7.13-cp310-cp310-macosx_11_0_arm64.whl",
                "reloadium-0.7.13-cp310-cp310-macosx_10_15_x86_64.whl"
        ));
    }

    @Test
    public void testDirNames() throws Exception {
        List<String> filenames = this.packageManager.getWheels().stream().map(f -> f.getDstDirName()).collect(Collectors.toList());

        assertThat(filenames).isEqualTo(List.of(
                "3.7", "3.8_arm64", "3.8", "3.9_arm64", "3.9", "3.10_arm64", "3.10"
        ));
    }
}
