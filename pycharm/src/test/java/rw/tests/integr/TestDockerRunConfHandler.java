package rw.tests.integr;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.jetbrains.python.run.PythonRunConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rw.action.RunType;
import rw.handler.DockerRunConfHandler;
import rw.handler.PythonRunConfHandler;
import rw.handler.RunConfHandlerFactory;
import rw.tests.BaseTestCase;
import rw.tests.fixtures.CakeshopFixture;

import static org.assertj.core.api.Assertions.assertThat;

public class TestDockerRunConfHandler extends BaseTestCase {
    CakeshopFixture cakeshop;
    DockerRunConfHandler handler;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        this.cakeshop = new CakeshopFixture(this.f);
        this.cakeshop.setUp();

        this.handler = new DockerRunConfHandler(this.cakeshop.getRunConf());
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.cakeshop.tearDown();

        super.tearDown();
    }

    @Test
    public void testBasic() {
        this.handler.beforeRun(RunType.RUN);

        PythonRunConfiguration runConf = (PythonRunConfiguration)this.handler.getRunConf();
        assertThat(runConf.getEnvs().get("RW_DOCKER")).isEqualTo("True");
    }
}
