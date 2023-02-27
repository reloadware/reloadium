package rw.tests.integr;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rw.action.RunType;
import rw.handler.DockerRunConfHandler;
import rw.tests.fixtures.CakeshopFixture;

import static org.assertj.core.api.Assertions.assertThat;

public class TestDockerRunConfHandler extends BasePlatformTestCase {
    CakeshopFixture cakeshop;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        this.cakeshop = new CakeshopFixture(this.getProject());
        this.cakeshop.start();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.cakeshop.stop();

        super.tearDown();
    }

    @Test
    public void testBasic() {
        DockerRunConfHandler remoteRunConfHandler = new DockerRunConfHandler(this.cakeshop.getRunConf());
        remoteRunConfHandler.beforeRun(RunType.RUN);

        assertThat(this.cakeshop.getRunConf().getEnvs().get("RW_DOCKER")).isEqualTo("True");
    }
}
