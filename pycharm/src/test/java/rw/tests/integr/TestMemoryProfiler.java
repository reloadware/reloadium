package rw.tests.integr;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import rw.profile.MemoryProfiler;
import rw.quickconfig.QuickConfig;
import rw.quickconfig.QuickConfigCallback;
import rw.quickconfig.QuickConfigState;
import rw.tests.BaseTestCase;

import static org.assertj.core.api.Assertions.assertThat;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestMemoryProfiler extends BaseTestCase {
    MemoryProfiler profiler;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        this.profiler = new MemoryProfiler(this.getProject(), new QuickConfig(new QuickConfigCallback() {
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
        assertThat(this.profiler.format(100L)).isEqualTo("100 B ");
        assertThat(this.profiler.format(999L)).isEqualTo("999 B ");
    }

    @Test
    public void testUs() throws Exception {
        assertThat(this.profiler.format(1000L)).isEqualTo("1.000 KB");
        assertThat(this.profiler.format(1100L)).isEqualTo("1.100 KB");
    }

    @Test
    public void testMs() throws Exception {
        assertThat(this.profiler.format(1_000_000L)).isEqualTo("1.000 MB");
        assertThat(this.profiler.format(1_001_000L)).isEqualTo("1.001 MB");
        assertThat(this.profiler.format(999_999L)).isEqualTo("999.999 KB");
    }

    @Test
    public void testS() throws Exception {
        assertThat(this.profiler.format(1_000_000_000L)).isEqualTo("1.000 GB ");
        assertThat(this.profiler.format(900_000_000L)).isEqualTo("900.000 MB");
    }
}
