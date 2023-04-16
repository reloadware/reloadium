package rw.tests;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import rw.audit.RwSentry;
import rw.pkg.PackageManager;
import rw.service.Service;
import rw.tests.fixtures.ConstFixture;
import rw.tests.fixtures.DialogFactoryFixture;
import rw.tests.fixtures.NativeFileSystemFixture;
import rw.tests.fixtures.SentryFixture;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.validateMockitoUsage;


@ExtendWith(MockitoExtension.class)
public class BaseMockedTestCase extends BasePlatformTestCase {
    public String webVersion = null;
    public String builtinVersion = null;

    public PackageManager packageManager;
    public NativeFileSystemFixture nativeFileSystemFixture;
    public DialogFactoryFixture dialogFactoryFixture;

    public ConstFixture constFixture;
    public SentryFixture sentryFixture;

    public RwSentry sentry;

    public Service service;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        this.nativeFileSystemFixture = new NativeFileSystemFixture();
        this.nativeFileSystemFixture.start();

        this.dialogFactoryFixture = new DialogFactoryFixture(this.getProject());
        this.dialogFactoryFixture.start();

        this.constFixture = new ConstFixture(true);
        this.constFixture.start();

        this.sentryFixture = new SentryFixture();
        this.sentry = this.sentryFixture.mocked;

        this.service = new Service();
        Service.singleton = this.service;

        this.packageManager = spy(this.service.packageManager);
        this.service.packageManager = this.packageManager;

        this.builtinVersion = "0.7.10";
        this.webVersion = "0.7.13";
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.constFixture.stop();
        this.dialogFactoryFixture.stop();
        this.nativeFileSystemFixture.stop();
        super.tearDown();
    }

    @AfterEach
    public void validate() {
        validateMockitoUsage();
    }
}