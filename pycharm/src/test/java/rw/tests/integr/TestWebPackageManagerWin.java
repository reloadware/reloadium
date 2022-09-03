package rw.tests.integr;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rw.pkg.Architecture;
import rw.pkg.wheel.BaseWheel;
import rw.tests.fixtures.OsFixture;
import rw.util.OsType;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;


public class TestWebPackageManagerWin extends TestWebPackageManager {
    OsFixture osFixture;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        this.osFixture = new OsFixture(OsType.Windows);
        this.osFixture.start();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.osFixture.stop();

        super.tearDown();
    }

    @Test
    public void testGetWheelFiles() throws Exception {
        List<String> filenames = this.webPackageManager.getWheelFiles().stream().map(f -> f.getName()).collect(Collectors.toList());

        assertThat(filenames).isEqualTo(List.of(
                "reloadium-0.7.13-cp310-cp310-win_amd64.whl",
                "reloadium-0.7.13-cp39-cp39-win_amd64.whl",
                "reloadium-0.7.13-cp38-cp38-win_amd64.whl",
                "reloadium-0.7.13-cp37-cp37m-win_amd64.whl"
        ));
    }

    @Test
    public void testGetLatestVersion() throws Exception {
        String latestVersion = this.webPackageManager.getLatestVersionFromWeb();
        assertThat(latestVersion).isEqualTo("0.7.13");
    }

    @Test
    public void testGetAllWheels() throws Exception {
        List<BaseWheel> urls = this.webPackageManager.getAllWheelUrls();
        assertThat(urls.size()).isEqualTo(8);

        BaseWheel wheel0 = urls.get(0);
        assertThat(wheel0.getInput()).isEqualTo(
                "http://localhost:1080/packages/9f/40/9de0e700da3ab14c248d744210fec04bad9777749141170ce1b72fb16b50/reloadium-0.7.13-cp310-cp310-win_amd64.whl#sha256=20c54a817bf15fb5a09ba795a03e5722d419c7bc3ff8b5c960d0b223e80c5029"
        );
        assertThat(wheel0.getArchitecture()).isEqualTo(Architecture.X64);
        assertThat(wheel0.getOsType()).isEqualTo(OsType.Windows);
        assertThat(wheel0.getVersion()).isEqualTo("0.7.13");
        assertThat(wheel0.getFilename()).isEqualTo("reloadium-0.7.13-cp310-cp310-win_amd64.whl");
        assertThat(wheel0.getPythonVersion()).isEqualTo("3.10");
    }

    @Test
    public void testGetWheelsForVersion() throws Exception {
        List<BaseWheel> urls = this.webPackageManager.getWheelUrlsForVersion("0.7.13");
        assertThat(urls.size()).isEqualTo(4);

        BaseWheel wheel0 = urls.get(0);
        assertThat(wheel0.getVersion()).isEqualTo("0.7.13");
    }
}
