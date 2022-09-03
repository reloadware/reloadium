package rw.tests.integr;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;
import rw.pkg.Architecture;
import rw.pkg.wheel.BaseWheel;
import rw.tests.BaseMockedTestCase;
import rw.tests.fixtures.ArchFixture;
import rw.tests.fixtures.M1Fixture;
import rw.tests.fixtures.OsFixture;
import rw.util.OsType;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


public class TestWebPackageManagerM1 extends BaseMockedTestCase {
    M1Fixture m1Fixture;

    @BeforeEach
    protected void setUp() throws Exception {
        this.m1Fixture = new M1Fixture();
        this.m1Fixture.start();

        super.setUp();

        Whitebox.setInternalState(OsType.class, "DETECTED", OsType.MacOS);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.m1Fixture.stop();
        super.tearDown();
    }

    @Test
    public void testGetLatestVersion() throws Exception {
        String latestVersion = this.webPackageManager.getLatestVersionFromWeb();
        assertThat(latestVersion).isEqualTo("0.7.13");
    }

    @Test
    public void testGetWheelFiles() throws Exception {
        List<String> filenames = this.webPackageManager.getWheelFiles().stream().map(f -> f.getName()).collect(Collectors.toList());

        assertThat(filenames).isEqualTo(List.of(
                "reloadium-0.7.13-cp310-cp310-macosx_11_0_arm64.whl",
                "reloadium-0.7.13-cp39-cp39-macosx_11_0_arm64.whl",
                "reloadium-0.7.13-cp38-cp38-macosx_11_0_arm64.whl"
        ));
    }

    @Test
    public void testGetAllWheels() throws Exception {
        List<BaseWheel> urls = this.webPackageManager.getAllWheelUrls();
        assertThat(urls.size()).isEqualTo(6);

        BaseWheel wheel0 = urls.get(0);
        assertThat(wheel0.getInput()).isEqualTo(
                "http://localhost:1080/packages/de/f0/c5b7f997486b00d1df45e9cdbe3391c869837154875cf151a51af76c1aa0/reloadium-0.7.13-cp310-cp310-macosx_11_0_arm64.whl#sha256=b545ebadaa2b878c8630e5bcdb97fc4096e779f335fc0f943547c1c91540c815"
        );
        assertThat(wheel0.getArchitecture()).isEqualTo(Architecture.ARM64);
        assertThat(wheel0.getOsType()).isEqualTo(OsType.MacOS);
        assertThat(wheel0.getVersion()).isEqualTo("0.7.13");
        assertThat(wheel0.getFilename()).isEqualTo("reloadium-0.7.13-cp310-cp310-macosx_11_0_arm64.whl");
        assertThat(wheel0.getPythonVersion()).isEqualTo("3.10");
    }

    @Test
    public void testGetWheelsForVersion() {
        List<BaseWheel> urls = this.webPackageManager.getWheelUrlsForVersion("0.7.13");
        assertThat(urls.size()).isEqualTo(3);

        BaseWheel wheel0 = urls.get(0);
        assertThat(wheel0.getVersion()).isEqualTo("0.7.13");
    }
}
