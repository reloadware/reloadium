package rw.tests;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.jetbrains.python.psi.LanguageLevel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import rw.action.DebugWithReloadium;
import rw.action.WithReloaderBase;
import rw.ai.tests.PythonTestUtil;
import rw.ai.tests.fixtures.PyLightProjectDescriptor;
import rw.audit.RwSentry;
import rw.pkg.PackageManager;
import rw.service.Service;
import rw.tests.fixtures.ConstFixture;
import rw.tests.fixtures.DialogFactoryFixture;
import rw.tests.fixtures.NativeFileSystemFixture;
import rw.tests.fixtures.SentryFixture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class BaseTestCase extends BasePlatformTestCase {
    public String webVersion = null;
    public String builtinVersion = null;

    public PackageManager packageManager;
    public NativeFileSystemFixture nativeFileSystemFixture;
    public DialogFactoryFixture dialogFactoryFixture;

    public ConstFixture constFixture;
    public SentryFixture sentryFixture;

    public RwSentry sentry;

    public Service service;

    protected CodeInsightTestFixture f;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        this.f = this.myFixture;

        this.nativeFileSystemFixture = new NativeFileSystemFixture();
        this.nativeFileSystemFixture.start();

        this.dialogFactoryFixture = new DialogFactoryFixture(this.getProject());
        this.dialogFactoryFixture.setUp();

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
        this.constFixture.tearDown();
        this.dialogFactoryFixture.tearDown();
        this.nativeFileSystemFixture.tearDown();
        super.tearDown();
    }


    @AfterEach
    public void validate() {
        validateMockitoUsage();
    }

    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        PyLightProjectDescriptor getProjectDescriptor = new PyLightProjectDescriptor(LanguageLevel.getLatest());
        return getProjectDescriptor;
    }

    public WithReloaderBase getWithReloaderBaseAction(String ID) {
        WithReloaderBase ret = (WithReloaderBase) spy(ActionManager.getInstance().getAction(ID));
        lenient().doNothing().when(ret).restartRunProfile(any(), any());
        return ret;
    }

    protected String getTestDataPath() {
        return PythonTestUtil.getTestDataPath();
    }
}