package rw.profile;

import org.jetbrains.annotations.Nullable;
import rw.util.colormap.ColorMap;
import rw.util.colormap.ColorMaps;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class FileValues {
    Map<Integer, Long> values;
    Map<Integer, String> display;
    Map<Integer, Color> colors;

    FileValues() {
        this.colors = new HashMap<>();
        this.values = new HashMap<>();
        this.display = new HashMap<>();
    }

    public void update(Map<Integer, Long> values, Map<Integer, String> display) {
        this.colors.clear();

        this.values.putAll(values);
        this.display.putAll(display);
        this.recalculateColors();
    }

    private void recalculateColors() {
        ColorMap colorMap = ColorMaps.get().getSelected();

        this.colors.clear();

        if (this.values.isEmpty()) {
            return;
        }

        Long min = Collections.min(this.values.values());
        Long max = Collections.max(this.values.values());

        for (Map.Entry<Integer, Long> entry : this.values.entrySet()) {
            Long lineTime = entry.getValue();

            Color color = colorMap.getColor(min, max, lineTime);
            this.colors.put(entry.getKey(), color);
        }
    }

    @Nullable
    public Color getLineColor(int line) {
        return this.colors.get(line+1);
    }

    @Nullable
    public String getLine(int line) {
        return this.display.get(line+1);
    }

    public Map<Integer, Color> getColors() {
        return colors;
    }

    public void clear() {
        this.colors.clear();
        this.values.clear();
    }

    public void clearLines(int start, int end) {
        for(int l: IntStream.rangeClosed(start, end).toArray()) {
            this.values.remove(l);
        }
    }
}
