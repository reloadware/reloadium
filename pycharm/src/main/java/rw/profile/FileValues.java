package rw.profile;

import com.google.common.collect.Lists;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.editor.FoldingModel;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.Nullable;
import rw.quickconfig.CumulateType;
import rw.util.colormap.ColorMap;
import rw.util.colormap.ColorMaps;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;


public class FileValues {
    Map<CumulateType, Map<Integer, Long>> values;
    Map<Integer, Long> hits;
    Map<Integer, Long> partials;
    Map<Integer, Long> lineToFrame;


    FileValues() {
        this.values = new HashMap<>();

        for (CumulateType c : CumulateType.getAll()) {
            this.values.put(c, new HashMap<>());
        }

        this.lineToFrame = new HashMap<>();
        this.hits = new HashMap<>();
        this.partials = new HashMap<>();
    }

    synchronized public void update(Map<Integer, Long> values, Long frameId, boolean partial) {
        for (Map.Entry<Integer, Long> entry : values.entrySet()) {
            if (entry.getValue() == 0) {
                continue;
            }

            int line = entry.getKey() - 1;
            Long value = entry.getValue();
            boolean newFrame = this.lineToFrame.get(line) == null || !this.lineToFrame.get(line).equals(frameId);

            if (newFrame && !partial) {
                this.hits.put(line, 0L);
                this.partials.put(line, 0L);
            }

            if(partial) {
                this.partials.put(line, this.partials.getOrDefault(line, 0L) + value);
            }

            Long currentAddValue = this.values.get(CumulateType.ADD).get(line);
            Long currentLastValue = this.values.get(CumulateType.LAST).get(line);
            Long currentMaxValue = this.values.get(CumulateType.MAX).get(line);
            Long currentMinValue = this.values.get(CumulateType.MIN).get(line);

            if (!partial) {
                this.hits.put(line, this.hits.getOrDefault(line, 0L) + 1);
            }
            Long hits = this.hits.get(line);

            if (currentAddValue == null || newFrame) {
                this.values.get(CumulateType.ADD).put(line, value);
            }
            else {
                if(partial) {
                    this.values.get(CumulateType.ADD).put(line, value + currentAddValue);
                }
                else {
                    this.values.get(CumulateType.ADD).put(line, currentAddValue + value - this.partials.getOrDefault(line, 0L));
                }
            }

            if (currentLastValue == null || newFrame) {
                this.values.get(CumulateType.LAST).put(line, value);
            }
            else {
                if(partial) {
                    this.values.get(CumulateType.LAST).put(line, this.partials.getOrDefault(line, 0L));
                }
                else {
                    this.values.get(CumulateType.LAST).put(line, value);
                }
            }

            if (!partial) {
                this.values.get(CumulateType.MEAN).put(line, this.values.get(CumulateType.ADD).get(line) / hits);

                if (currentMaxValue == null || newFrame || value > currentMaxValue) {
                    this.values.get(CumulateType.MAX).put(line, value);
                }

                if (currentMinValue == null || newFrame || value < currentMinValue) {
                    this.values.get(CumulateType.MIN).put(line, value);
                }
                this.partials.put(line, 0L);
            }
            this.lineToFrame.put(line, frameId);
        }
    }

    synchronized Map<Integer, Long> getFoldedValues(Editor editor, @Nullable Long frameId, CumulateType cumulateType) {
        final FoldingModel foldingModel = editor.getFoldingModel();
        Document document = editor.getDocument();

        Map<Integer, Long> ret = new HashMap<>();
        Map<Integer, Long> cumulateValues = new HashMap<>(this.values.get(cumulateType));

        for (Map.Entry<Integer, Long> entry : cumulateValues.entrySet()) {
            int line = entry.getKey();

            if (frameId != null) {
                if (!this.lineToFrame.get(line).equals(frameId)) {
                    continue;
                }
            }
            ret.put(line, entry.getValue());
        }

        for (FoldRegion region : Lists.reverse(Arrays.asList(foldingModel.getAllFoldRegions()))) {
            if (region.isExpanded()) {
                continue;
            }
            int startOffset = region.getStartOffset();
            int endOffset = region.getEndOffset();

            int startLine = document.getLineNumber(startOffset);
            int endLine = document.getLineNumber(endOffset);

            Long sum = null;
            for (Integer l : IntStream.rangeClosed(startLine, endLine).toArray()) {
                if (!ret.containsKey(l)) {
                    continue;
                }

                if (sum == null) {
                    sum = 0L;
                }
                sum += ret.getOrDefault(l, 0L);
            }

            if (sum == null) {
                continue;
            }

            for (Integer l : IntStream.rangeClosed(startLine, endLine).toArray()) {
                ret.put(l, sum);
            }
        }

        return ret;
    }

    @Nullable
    synchronized public Color getLineColor(int line, Editor editor, boolean frameScope, CumulateType cumulateType) {
        Long frameId;

        if (frameScope) {
            frameId = this.lineToFrame.get(line);
        } else {
            frameId = null;
        }

        Map<Integer, Long> values = this.getFoldedValues(editor, frameId, cumulateType);

        if (values.isEmpty()) {
            return null;
        }

        Long min = Collections.min(values.values());
        Long max = Collections.max(values.values());

        ColorMap colorMap = ColorMaps.get().getSelected();

        if (values.isEmpty()) {
            return null;
        }

        Long value = values.get(line);

        if (value == null) {
            return null;
        }

        Color color = colorMap.getColor(min, max, value);
        return color;
    }

    @Nullable
    synchronized public Long getValue(int line, Editor editor, CumulateType cumulateType) {
        return this.getFoldedValues(editor, null, cumulateType).get(line);
    }

    synchronized public void clear() {
        for (CumulateType c : CumulateType.getAll()) {
            this.values.get(c).clear();
        }
    }

    synchronized public Map<Integer, Long> getValues(CumulateType cumulateType) {
        return this.values.get(cumulateType);
    }

    synchronized public void clear(int start, int end) {
        for (int l : IntStream.rangeClosed(start, end).toArray()) {
            for (CumulateType c : CumulateType.getAll()) {
                this.values.get(c).remove(l);
            }
        }
    }

    synchronized public boolean isEmpty() {
        return this.values.get(CumulateType.DEFAULT).isEmpty();
    }
}
