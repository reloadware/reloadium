package rw.session;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.xdebugger.ui.DebuggerColors;
import org.jetbrains.annotations.VisibleForTesting;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rw.session.Highlighter;

import static com.intellij.codeInsight.hint.HintUtil.ERROR_COLOR_KEY;
import static com.intellij.openapi.editor.colors.EditorColorsUtil.getGlobalOrDefaultColor;


public class HighlightManager {
    Map<File, List<Highlighter>> all;

    @VisibleForTesting
    public static HighlightManager singleton;

    @VisibleForTesting
    public HighlightManager() {
        this.all = new HashMap<>();
    }

    public static HighlightManager get() {
        if (HighlightManager.singleton == null) {
            HighlightManager.singleton = new HighlightManager();
        }
        return HighlightManager.singleton;
    }

    public void add(Project project, File file, int line) {
        if (!this.all.containsKey(file)) {
            this.all.put(file, new ArrayList<>());
        }

        VirtualFile virtualFile = new VirtualFileWrapper(file).getVirtualFile();
        Document document = ReadAction.compute(() -> FileDocumentManager.getInstance().getDocument(virtualFile));

        MarkupModel markupModel = DocumentMarkupModel.forDocument(document, project, true);
        TextAttributes textAttribute = new TextAttributes(null, getGlobalOrDefaultColor(ERROR_COLOR_KEY),
                null, null, Font.PLAIN);

        ApplicationManager.getApplication().invokeLater(() -> {
            RangeHighlighter highlighter = markupModel.addLineHighlighter(line-1,
                DebuggerColors.EXECUTION_LINE_HIGHLIGHTERLAYER, textAttribute);
            this.all.get(file).add(new Highlighter(highlighter, markupModel));
        });
    }

    public void clearFile(File file) {
        List<Highlighter> highlighters = this.all.get(file);
        if (highlighters == null) {
            return;
        }

        ApplicationManager.getApplication().invokeLater(() -> {
            for (Highlighter h : highlighters) {
                h.remove();
            }
            highlighters.clear();
        });
    }

    public void clearAll() {
        for(File f: this.all.keySet()) {
            this.clearFile(f);
        }
    }
}
