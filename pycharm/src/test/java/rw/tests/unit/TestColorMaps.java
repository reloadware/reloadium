package rw.tests.unit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import rw.util.colormap.ColorMap;
import rw.util.colormap.ColorMaps;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestColorMaps {

    Color minColor;
    Color halfColor;
    Color maxColor;
    ColorMap viridis;

    @BeforeEach
    protected void setUp() throws Exception {
        this.minColor = new Color(0.267004f, 0.004874f, 0.329415f);
        this.halfColor = new Color(0.128729f, 0.563265f, 0.551229f);
        this.maxColor = new Color(0.993248f, 0.906157f, 0.143936f);
        this.viridis = ColorMaps.get().viridis;
    }

    @AfterEach
    protected void tearDown() throws Exception {
    }

    @Test
    public void testMin() throws Exception {

        assertThat(viridis.getColor(0.0f, 1.0f, 0.0f)).isEqualTo(this.minColor);
    }

    @Test
    public void testMin2() throws Exception {
        assertThat(viridis.getColor(0.5f, 2.5f, 0.5f)).isEqualTo(this.minColor);
    }

    @Test
    public void testMax() throws Exception {
        assertThat(viridis.getColor(0.0f, 1.0f, 1.0f)).isEqualTo(this.maxColor);
    }

    @Test
    public void testMax2() throws Exception {
        assertThat(viridis.getColor(0.5f, 2.5f, 2.5f)).isEqualTo(this.maxColor);
    }

    @Test
    public void testHalf() throws Exception {
        assertThat(viridis.getColor(0.0f, 1.0f, 0.5f)).isEqualTo(this.halfColor);
    }

    @Test
    public void testHalf2() throws Exception {
        assertThat(viridis.getColor(0.4f, 0.8f, 0.6f)).isEqualTo(this.halfColor);
    }

    @Test
    public void testOutOfRangeLower() throws Exception {
        assertThat(viridis.getColor(0.0f, 1.0f, -0.5f)).isEqualTo(this.minColor);
    }

    @Test
    public void testOutOfRangeHigher() throws Exception {
        assertThat(viridis.getColor(0.0f, 1.0f, 20.5f)).isEqualTo(this.maxColor);
    }
}
