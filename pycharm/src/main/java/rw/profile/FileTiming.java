package rw.profile;

import org.jetbrains.annotations.Nullable;
import rw.util.colormap.ColorMap;
import rw.util.colormap.ColorMaps;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class FileTiming {
    Map<Integer, Long> timing;
    Map<Integer, Color> colors;

    FileTiming() {
        this.colors = new HashMap<>();
        this.timing = new HashMap<>();
    }

    public void update(Map<Integer, Long> lineTiming) {
        this.updateTiming(lineTiming);
    }
    public Map<Integer, Color> getLineColors() {
        ColorMap colorMap = ColorMaps.get().getSelected();

        Map<Integer, Color> ret = new HashMap<>();

        Map<Integer, Long> timing = new HashMap<>(this.timing);

        if (timing.isEmpty()) {
            return ret;
        }

        Long min = Collections.min(timing.values());
        Long max = Collections.max(timing.values());

        for (Map.Entry<Integer, Long> entry : timing.entrySet()) {
            Long lineTime = entry.getValue();

            if (lineTime == null) {
                return null;
            }

            Color color = colorMap.getColor(min, max, lineTime);
            ret.put(entry.getKey(), color);
        }
        return ret;
    }

    public void updateTiming(Map<Integer, Long> timing) {
        this.colors.clear();

        this.timing.putAll(timing);
        this.recalculateColors();
    }

    private void recalculateColors() {
        ColorMap colorMap = ColorMaps.get().getSelected();

        this.colors.clear();

        if (this.timing.isEmpty()) {
            return;
        }

        Long min = Collections.min(this.timing.values());
        Long max = Collections.max(this.timing.values());

        for (Map.Entry<Integer, Long> entry : this.timing.entrySet()) {
            Long lineTime = entry.getValue();

            Color color = colorMap.getColor(min, max, lineTime);
            this.colors.put(entry.getKey(), color);
        }
    }

    @Nullable
    public Color getLineColor(int line) {
        return this.colors.get(line);
    }

    @Nullable
    public Float getLineTimeMs(int line) {
        Long time = this.getLineTimeNs(line);
        if (time == null) {
            return null;
        }

        float ret = time / 1e6f;
        return ret;
    }

    @Nullable
    public Long getLineTimeNs(int line) {
        int cleanLine = line + 1;
        Long ret = this.timing.get(cleanLine);
        return ret;
    }

    public Map<Integer, Color> getColors() {
        return colors;
    }

    public void clear() {
        this.colors.clear();
        this.timing.clear();
    }

    public void clearLines(int start, int end) {
        for(int l: IntStream.rangeClosed(start, end).toArray()) {
            this.timing.remove(l);
        }
    }
}
