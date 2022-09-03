package rw.tests.integr;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;
import rw.pkg.Architecture;
import rw.pkg.wheel.BaseWheel;
import rw.tests.BaseMockedTestCase;
import rw.tests.fixtures.MacFixture;
import rw.tests.fixtures.OsFixture;
import rw.util.OsType;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;


public class TestWebPackageManagerMac extends BaseMockedTestCase {
    MacFixture macFixture;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        this.macFixture = new MacFixture();
        this.macFixture.start();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.macFixture.stop();
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
                "reloadium-0.7.13-cp310-cp310-macosx_10_15_x86_64.whl",
                "reloadium-0.7.13-cp39-cp39-macosx_10_9_x86_64.whl",
                "reloadium-0.7.13-cp38-cp38-macosx_10_9_x86_64.whl",
                "reloadium-0.7.13-cp37-cp37m-macosx_10_9_x86_64.whl"
        ));
    }

    @Test
    public void testGetAllWheels() throws Exception {
        List<BaseWheel> urls = this.webPackageManager.getAllWheelUrls();
        assertThat(urls.size()).isEqualTo(8);

        BaseWheel wheel0 = urls.get(0);
        assertThat(wheel0.getInput()).isEqualTo(
                "http://localhost:1080/packages/b6/ce/28c5d8fe1ca780021bdaf12ae41ea0b6016e9d22914a369d4e358fd250ae/reloadium-0.7.13-cp310-cp310-macosx_10_15_x86_64.whl#sha256=bedf32e39f365ed0b1e020ce6154d53d4e9200e64ea4d93c7efebef03c0e49d0"
        );
        assertThat(wheel0.getArchitecture()).isEqualTo(Architecture.X64);
        assertThat(wheel0.getOsType()).isEqualTo(OsType.MacOS);
        assertThat(wheel0.getVersion()).isEqualTo("0.7.13");
        assertThat(wheel0.getFilename()).isEqualTo("reloadium-0.7.13-cp310-cp310-macosx_10_15_x86_64.whl");
        assertThat(wheel0.getPythonVersion()).isEqualTo("3.10");
    }

    @Test
    public void testGetWheelsForVersion() {
        List<BaseWheel> urls = this.webPackageManager.getWheelUrlsForVersion("0.7.13");
        assertThat(urls.size()).isEqualTo(4);

        BaseWheel wheel0 = urls.get(0);
        assertThat(wheel0.getVersion()).isEqualTo("0.7.13");
    }
}
