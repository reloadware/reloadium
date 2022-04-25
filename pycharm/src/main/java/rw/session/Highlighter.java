package rw.session;

import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;

public class Highlighter {
    RangeHighlighter highlighter;
    MarkupModel markupModel;

    Highlighter(RangeHighlighter highlighter, MarkupModel markupModel) {
        this.highlighter = highlighter;
        this.markupModel = markupModel;
    }

    public void remove() {
        this.markupModel.removeHighlighter(this.highlighter);
    }
}
