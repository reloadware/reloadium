package rw.tests;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import rw.audit.RwSentry;
import rw.pkg.BuiltinPackageManager;
import rw.service.Service;
import rw.tests.fixtures.ConfigFixture;
import rw.tests.fixtures.PyPiFixture;
import rw.tests.fixtures.SentryFixture;
import rw.pkg.WebPackageManager;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.validateMockitoUsage;


@ExtendWith(MockitoExtension.class)
public class BaseMockedTestCase extends BasePlatformTestCase {
    public String webVersion = null;
    public String builtinVersion = null;

    public PyPiFixture pyPiFixture;
    public WebPackageManager webPackageManager;
    public BuiltinPackageManager builtinPackageManager;

    public ConfigFixture configFixture;
    public SentryFixture sentryFixture;

    public RwSentry sentry;

    public Service service;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        this.configFixture = new ConfigFixture(true);
        this.configFixture.start();

        this.sentryFixture = new SentryFixture();
        this.sentry = this.sentryFixture.mocked;

        this.pyPiFixture = new PyPiFixture();
        this.pyPiFixture.start();
        this.pyPiFixture.mockSimple();

        this.service = spy(new Service());
        Service.singleton = this.service;

        this.webPackageManager = spy(this.service.webPackageManager);
        this.service.webPackageManager = this.webPackageManager;

        this.builtinPackageManager = spy(this.service.builtinPackageManager);
        this.service.builtinPackageManager = this.builtinPackageManager;

        this.builtinVersion = "0.7.10";
        this.webVersion = "0.7.13";
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.pyPiFixture.stop();
        this.configFixture.stop();
        super.tearDown();
    }

    @AfterEach
    public void validate() {
        validateMockitoUsage();
    }
}