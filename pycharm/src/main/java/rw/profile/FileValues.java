package rw.profile;

import com.google.common.collect.Lists;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.editor.FoldingModel;
import org.jetbrains.annotations.Nullable;
import rw.quickconfig.CumulateType;
import rw.util.colormap.ColorMap;
import rw.util.colormap.ColorMaps;

import java.awt.*;
import java.util.*;
import java.util.stream.IntStream;


public class FileValues {
    Map<CumulateType, Map<Integer, Long>> values;
    Map<Integer, String> lineToFrame;

    FileValues() {
        this.values = new HashMap<>();

        for(CumulateType c: CumulateType.getAll()) {
            this.values.put(c, new HashMap<>());
        }

        this.lineToFrame = new HashMap<>();
    }

    public void update(Map<Integer, Long> values, String frame, Integer frameLine, CumulateType cumulateType) {
        for (Map.Entry<Integer, Long> entry : values.entrySet()) {
            Long currentAddValue = this.values.get(CumulateType.ADD).getOrDefault(entry.getKey(), 0L);
            Long currentMeanValue = this.values.get(CumulateType.MEAN).getOrDefault(entry.getKey(), entry.getValue());
            Long currentMaxValue = this.values.get(CumulateType.MAX).getOrDefault(entry.getKey(), 0L);
            Long currentMinValue = this.values.get(CumulateType.MAX).getOrDefault(entry.getKey(), Long.MAX_VALUE);

            this.values.get(CumulateType.ADD).put(entry.getKey(),
                    entry.getValue() + currentAddValue);
            this.values.get(CumulateType.MEAN).put(entry.getKey(), (entry.getValue() + currentMeanValue) / 2L);
            if (entry.getValue() > currentMaxValue) {
                this.values.get(CumulateType.MAX).put(entry.getKey(), entry.getValue());
            }
            if (entry.getValue() < currentMinValue) {
                this.values.get(CumulateType.MIN).put(entry.getKey(), entry.getValue());
            }

            this.values.get(CumulateType.LAST).put(entry.getKey(), entry.getValue());
        }

        this.lineToFrame.put(frameLine - 1, frame);

        for (Integer l : values.keySet()) {
            this.lineToFrame.put(l - 1, frame);
        }
    }

    Map<Integer, Long> getFoldedValues(Editor editor, @Nullable String frame, CumulateType cumulateType) {
        final FoldingModel foldingModel = editor.getFoldingModel();
        Document document = editor.getDocument();

        Map<Integer, Long> ret = new HashMap<>();
        Map<Integer, Long> cumulateValues = this.values.get(cumulateType);

        for (Map.Entry<Integer, Long> entry : cumulateValues.entrySet()) {
            int line = entry.getKey() - 1;

            if (frame != null) {
                if (!this.lineToFrame.get(line).equals(frame)) {
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
    public Color getLineColor(int line, Editor editor, boolean frameScope, CumulateType cumulateType) {
        String frame;

        if (frameScope) {
            frame = this.lineToFrame.get(line);
        } else {
            frame = null;
        }

        Map<Integer, Long> values = this.getFoldedValues(editor, frame, cumulateType);

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
    public Long getValue(int line, Editor editor, CumulateType cumulateType) {
        return this.getFoldedValues(editor, null, cumulateType).get(line);
    }

    public void clear() {
        for(CumulateType c: CumulateType.getAll()) {
            this.values.get(c).clear();
        }
    }

    public Map<Integer, Long> getValues(CumulateType cumulateType) {
        return this.values.get(cumulateType);
    }

    public void clear(int start, int end) {
        for (int l : IntStream.rangeClosed(start, end).toArray()) {
            for (CumulateType c : CumulateType.getAll()) {
                this.values.get(c).remove(l);
            }
        }
    }
}