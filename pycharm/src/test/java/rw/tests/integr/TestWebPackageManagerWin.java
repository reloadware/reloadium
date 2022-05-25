package rw.tests.integr;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rw.pkg.Architecture;
import rw.pkg.wheel.BaseWheel;
import rw.tests.fixtures.WinFixture;
import rw.util.OsType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;


public class TestWebPackageManagerWin extends TestWebPackageManager {
    WinFixture winFixture;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        this.winFixture = new WinFixture();
        this.winFixture.start();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.winFixture.stop();
        super.tearDown();
    }

    @Test
    public void testGetLatestVersion() throws Exception {
        String latestVersion = this.webPackageManager.getLatestVersionFromWeb();
        assertThat(latestVersion).isEqualTo("0.7.13");
    }

    @Test
    public void testGetAllWheels() throws Exception {
        List<BaseWheel> urls = this.webPackageManager.getAllWheelUrls();
        assertThat(urls.size()).isEqualTo(20);

        BaseWheel wheel0 = urls.get(0);
        assertThat(wheel0.getInput()).isEqualTo(
                "http://localhost:1080/packages/de/f0/c5b7f997486b00d1df45e9cdbe3391c869837154875cf151a51af76c1aa0/reloadium-0.7.13-cp310-cp310-win32.whl#sha256=3c873beb69bfe907685ca083075c15597815e8e7806f5783be20c54c208028d8"
        );
        assertThat(wheel0.getArchitecture()).isEqualTo(Architecture.x86);
        assertThat(wheel0.getOsType()).isEqualTo(OsType.Windows);
        assertThat(wheel0.getVersion()).isEqualTo("0.7.13");
        assertThat(wheel0.getFilename()).isEqualTo("reloadium-0.7.13-cp310-cp310-win32.whl");
        assertThat(wheel0.getPythonVersion()).isEqualTo("3.10");

        BaseWheel wheel1 = urls.get(1);
        assertThat(wheel1.getInput()).isEqualTo(
                "http://localhost:1080/packages/9f/40/9de0e700da3ab14c248d744210fec04bad9777749141170ce1b72fb16b50/reloadium-0.7.13-cp310-cp310-win_amd64.whl#sha256=20c54a817bf15fb5a09ba795a03e5722d419c7bc3ff8b5c960d0b223e80c5029"
        );
        assertThat(wheel1.getArchitecture()).isEqualTo(Architecture.x64);
        assertThat(wheel1.getOsType()).isEqualTo(OsType.Windows);
        assertThat(wheel1.getVersion()).isEqualTo("0.7.13");
        assertThat(wheel1.getFilename()).isEqualTo("reloadium-0.7.13-cp310-cp310-win_amd64.whl");
        assertThat(wheel1.getPythonVersion()).isEqualTo("3.10");
    }

    @Test
    public void testGetWheelsForVersion() throws Exception {
        List<BaseWheel> urls = this.webPackageManager.getWheelUrlsForVersion("0.7.13");
        assertThat(urls.size()).isEqualTo(10);

        BaseWheel wheel0 = urls.get(0);
        assertThat(wheel0.getVersion()).isEqualTo("0.7.13");
    }
}
