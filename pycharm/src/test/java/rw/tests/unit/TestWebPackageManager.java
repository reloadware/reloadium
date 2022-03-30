package rw.tests.unit;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rw.config.Config;
import rw.pkg.Architecture;
import rw.pkg.wheel.BaseWheel;
import rw.tests.BaseMockedTestCase;
import rw.tests.fixtures.PackageFixture;
import rw.util.OsType;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class TestWebPackageManager extends BaseMockedTestCase {
    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

    }

    @AfterEach
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testGetLatestVersion() throws Exception {
        String latestVersion = this.webPackageManager.getLatestVersionFromWeb();
        assertThat(latestVersion).isEqualTo("0.7.13");
    }

    @Test
    public void testShouldInstallCurrentOld() throws Exception {
        PackageFixture packageFixture = new PackageFixture("0.1.0");
        assertThat(this.webPackageManager.shouldInstall()).isTrue();
    }

    @Test
    public void testShouldInstallCurrentSame() throws Exception {
        PackageFixture packageFixture = new PackageFixture(this.webVersion);
        assertThat(this.webPackageManager.shouldInstall()).isFalse();
    }

    @Test
    public void testGetAllWheels() throws Exception {
        List<BaseWheel> urls = this.webPackageManager.getAllWheelUrls();
        assertThat(urls.size()).isEqualTo(10);

        BaseWheel wheel0 = urls.get(0);
        assertThat(wheel0.getInput()).isEqualTo(
                "http://localhost:1080/packages/45/2f/ad446089f3b4bdf69fd9911a386fcd0de14aed0d8be6195c4d098d4b52e9/reloadium-0.7.13-cp310-cp310-manylinux_2_17_x86_64.manylinux2014_x86_64.whl#sha256=c494b2bc7e0412cf08bdfd047e7c8dc74dcf68c7bbe2250b395a1525d0ad306a"
        );
        assertThat(wheel0.getArchitecture()).isEqualTo(Architecture.x86_64);
        assertThat(wheel0.getOsType()).isEqualTo(OsType.Linux);
        assertThat(wheel0.getVersion()).isEqualTo("0.7.13");
        assertThat(wheel0.getFilename()).isEqualTo("reloadium-0.7.13-cp310-cp310-manylinux_2_17_x86_64.manylinux2014_x86_64.whl");
        assertThat(wheel0.getPythonVersion()).isEqualTo("3.10");

        BaseWheel wheel1 = urls.get(1);
        assertThat(wheel1.getInput()).isEqualTo(
                "http://localhost:1080/packages/b3/4e/e118b00b5019a2e014e67ccdf7f728bee580070c95cb14b714284bcb24f9/reloadium-0.7.13-cp39-cp39-manylinux_2_17_x86_64.manylinux2014_x86_64.whl#sha256=f6a6ac514a0db9a9fb47d1ed806e9d663157d5f9205c7056a7f21ac195809fec"
        );
        assertThat(wheel1.getArchitecture()).isEqualTo(Architecture.x86_64);
        assertThat(wheel1.getOsType()).isEqualTo(OsType.Linux);
        assertThat(wheel1.getVersion()).isEqualTo("0.7.13");
        assertThat(wheel1.getFilename()).isEqualTo("reloadium-0.7.13-cp39-cp39-manylinux_2_17_x86_64.manylinux2014_x86_64.whl");
        assertThat(wheel1.getPythonVersion()).isEqualTo("3.9");

        BaseWheel wheel2 = urls.get(2);
        assertThat(wheel2.getInput()).isEqualTo(
                "http://localhost:1080/packages/91/86/61b011f1e470a523280d3810a756532e3b2e0b36900f794bde78754137db/reloadium-0.7.13-cp38-cp38-manylinux_2_17_x86_64.manylinux2014_x86_64.whl#sha256=f4f7b1f035ac2299170ec4da078ce6c4c2bccec6705893ee706a44fd8b953155"
        );
        assertThat(wheel2.getArchitecture()).isEqualTo(Architecture.x86_64);
        assertThat(wheel2.getOsType()).isEqualTo(OsType.Linux);
        assertThat(wheel2.getVersion()).isEqualTo("0.7.13");
        assertThat(wheel2.getFilename()).isEqualTo("reloadium-0.7.13-cp38-cp38-manylinux_2_17_x86_64.manylinux2014_x86_64.whl");
        assertThat(wheel2.getPythonVersion()).isEqualTo("3.8");

        BaseWheel wheel3 = urls.get(3);
        assertThat(wheel3.getInput()).isEqualTo(
                "http://localhost:1080/packages/95/10/a32c1a24604c05600806ce8c7691c877ad00380d4eb838af11894a12cbf2/reloadium-0.7.13-cp37-cp37m-manylinux_2_17_x86_64.manylinux2014_x86_64.whl#sha256=b8460efbece651639be6942fbd4049a8f8abeae93ac3684c9690c4146b21b1a8"
        );
        assertThat(wheel3.getArchitecture()).isEqualTo(Architecture.x86_64);
        assertThat(wheel3.getOsType()).isEqualTo(OsType.Linux);
        assertThat(wheel3.getVersion()).isEqualTo("0.7.13");
        assertThat(wheel3.getFilename()).isEqualTo("reloadium-0.7.13-cp37-cp37m-manylinux_2_17_x86_64.manylinux2014_x86_64.whl");
        assertThat(wheel3.getPythonVersion()).isEqualTo("3.7");

        BaseWheel wheel4 = urls.get(4);
        assertThat(wheel4.getInput()).isEqualTo(
                "http://localhost:1080/packages/02/b5/b0e1a245ba7e8823e68a1e278589f1bceec6b6a79ae472fe6942f78247af/reloadium-0.7.13-cp36-cp36m-manylinux_2_17_x86_64.manylinux2014_x86_64.whl#sha256=06baedfb490e66127c15aaeb9100b8c7b89744924b3cdbce903bb64369922712"
        );
        assertThat(wheel4.getArchitecture()).isEqualTo(Architecture.x86_64);
        assertThat(wheel4.getOsType()).isEqualTo(OsType.Linux);
        assertThat(wheel4.getVersion()).isEqualTo("0.7.13");
        assertThat(wheel4.getFilename()).isEqualTo("reloadium-0.7.13-cp36-cp36m-manylinux_2_17_x86_64.manylinux2014_x86_64.whl");
        assertThat(wheel4.getPythonVersion()).isEqualTo("3.6");

        // 0.7.12
        BaseWheel wheel5 = urls.get(5);
        assertThat(wheel5.getInput()).isEqualTo(
                "http://localhost:1080/packages/45/2f/ad446089f3b4bdf69fd9911a386fcd0de14aed0d8be6195c4d098d4b52e9/reloadium-0.7.12-cp310-cp310-manylinux_2_17_x86_64.manylinux2014_x86_64.whl#sha256=c494b2bc7e0412cf08bdfd047e7c8dc74dcf68c7bbe2250b395a1525d0ad306a"
        );
        assertThat(wheel5.getArchitecture()).isEqualTo(Architecture.x86_64);
        assertThat(wheel5.getOsType()).isEqualTo(OsType.Linux);
        assertThat(wheel5.getVersion()).isEqualTo("0.7.12");
        assertThat(wheel5.getFilename()).isEqualTo("reloadium-0.7.12-cp310-cp310-manylinux_2_17_x86_64.manylinux2014_x86_64.whl");
        assertThat(wheel5.getPythonVersion()).isEqualTo("3.10");

        BaseWheel wheel6 = urls.get(6);
        assertThat(wheel6.getInput()).isEqualTo(
                "http://localhost:1080/packages/45/2f/ad446089f3b4bdf69fd9911a386fcd0de14aed0d8be6195c4d098d4b52e9/reloadium-0.7.12-cp39-cp39-manylinux_2_17_x86_64.manylinux2014_x86_64.whl#sha256=c494b2bc7e0412cf08bdfd047e7c8dc74dcf68c7bbe2250b395a1525d0ad306a"
        );
        assertThat(wheel6.getArchitecture()).isEqualTo(Architecture.x86_64);
        assertThat(wheel6.getOsType()).isEqualTo(OsType.Linux);
        assertThat(wheel6.getVersion()).isEqualTo("0.7.12");
        assertThat(wheel6.getFilename()).isEqualTo("reloadium-0.7.12-cp39-cp39-manylinux_2_17_x86_64.manylinux2014_x86_64.whl");
        assertThat(wheel6.getPythonVersion()).isEqualTo("3.9");

        BaseWheel wheel7 = urls.get(7);
        assertThat(wheel7.getInput()).isEqualTo(
                "http://localhost:1080/packages/09/fe/aadfebf13cc29d5e458f6da6ae7e39246fc7f0566988f2b36d94acb3f203/reloadium-0.7.12-cp38-cp38-manylinux_2_17_x86_64.manylinux2014_x86_64.whl#sha256=87dd0ee4cf8f965bb8176b68ba59d9f422aa3c3d5d2e317ad1750654afe7cb1e"
        );
        assertThat(wheel7.getArchitecture()).isEqualTo(Architecture.x86_64);
        assertThat(wheel7.getOsType()).isEqualTo(OsType.Linux);
        assertThat(wheel7.getVersion()).isEqualTo("0.7.12");
        assertThat(wheel7.getFilename()).isEqualTo("reloadium-0.7.12-cp38-cp38-manylinux_2_17_x86_64.manylinux2014_x86_64.whl");
        assertThat(wheel7.getPythonVersion()).isEqualTo("3.8");

        BaseWheel wheel8 = urls.get(8);
        assertThat(wheel8.getInput()).isEqualTo(
                "http://localhost:1080/packages/30/40/c024ab17b3efb04559a1efe775856b1018296f88128c8968188dc52dbf38/reloadium-0.7.12-cp37-cp37m-manylinux_2_17_x86_64.manylinux2014_x86_64.whl#sha256=4eba279ce78c91cf5b1ede0544469ea7697cc536a18619570d9d809aebec08be"
        );
        assertThat(wheel8.getArchitecture()).isEqualTo(Architecture.x86_64);
        assertThat(wheel8.getOsType()).isEqualTo(OsType.Linux);
        assertThat(wheel8.getVersion()).isEqualTo("0.7.12");
        assertThat(wheel8.getFilename()).isEqualTo("reloadium-0.7.12-cp37-cp37m-manylinux_2_17_x86_64.manylinux2014_x86_64.whl");
        assertThat(wheel8.getPythonVersion()).isEqualTo("3.7");

        BaseWheel wheel9 = urls.get(9);
        assertThat(wheel9.getInput()).isEqualTo(
                "http://localhost:1080/packages/53/be/53d62c05408ee925fc8a8fdce03bf35a9697bcc9604c3d21e3fb7be90f93/reloadium-0.7.12-cp36-cp36m-manylinux_2_17_x86_64.manylinux2014_x86_64.whl#sha256=70029eed62ae3384b0eb1f3763decb67dcf849fbf5acbae321da54303e59ae16"
        );
        assertThat(wheel9.getArchitecture()).isEqualTo(Architecture.x86_64);
        assertThat(wheel9.getOsType()).isEqualTo(OsType.Linux);
        assertThat(wheel9.getVersion()).isEqualTo("0.7.12");
        assertThat(wheel9.getFilename()).isEqualTo("reloadium-0.7.12-cp36-cp36m-manylinux_2_17_x86_64.manylinux2014_x86_64.whl");
        assertThat(wheel9.getPythonVersion()).isEqualTo("3.6");
    }

    @Test
    public void testGetWheelsForVersion() throws Exception {
        List<BaseWheel> urls = this.webPackageManager.getWheelUrlsForVersion("0.7.13");
        assertThat(urls.size()).isEqualTo(5);

        BaseWheel wheel0 = urls.get(0);
        assertThat(wheel0.getVersion()).isEqualTo("0.7.13");
    }

    @Test
    public void testErrored() throws Exception {
        lenient().doThrow(new RuntimeException("Error")).when(
                this.service.webPackageManager).getWheelUrlsForVersion(any());

        FileUtils.deleteDirectory(Config.get().getPackagesRootDir());

        this.service.checkForUpdate();

        verify(this.service.webPackageManager, times(1)).install(any());
        verify(this.sentry, times(1)).captureException(any());
    }
}
