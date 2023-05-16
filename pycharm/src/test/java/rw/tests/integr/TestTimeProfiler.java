package rw.tests.integr;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import rw.profile.TimeProfiler;
import rw.quickconfig.QuickConfig;
import rw.quickconfig.QuickConfigCallback;
import rw.quickconfig.QuickConfigState;
import rw.tests.BaseTestCase;

import static org.assertj.core.api.Assertions.assertThat;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestTimeProfiler extends BaseTestCase {
    TimeProfiler profiler;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        this.profiler = new TimeProfiler(this.getProject(), new QuickConfig(new QuickConfigCallback() {
            @Override
            public void onChange(QuickConfigState state) {

            }
        }));
    }

    @AfterEach
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testPs() throws Exception {
        assertThat(this.profiler.format(100L)).isEqualTo("100 ps");
        assertThat(this.profiler.format(999L)).isEqualTo("999 ps");
    }

    @Test
    public void testUs() throws Exception {
        assertThat(this.profiler.format(1000L)).isEqualTo("1.000 us");
        assertThat(this.profiler.format(1100L)).isEqualTo("1.100 us");
    }

    @Test
    public void testMs() throws Exception {
        assertThat(this.profiler.format(1_000_000L)).isEqualTo("1.000 ms");
        assertThat(this.profiler.format(1_001_000L)).isEqualTo("1.001 ms");
        assertThat(this.profiler.format(999_999L)).isEqualTo("999.999 us");
    }

    @Test
    public void testS() throws Exception {
        assertThat(this.profiler.format(1_000_000_000L)).isEqualTo("1.000 s ");
        assertThat(this.profiler.format(900_000_000L)).isEqualTo("900.000 ms");
    }
}
