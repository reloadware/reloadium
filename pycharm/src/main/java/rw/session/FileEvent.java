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
import rw.handler.runConf.PythonRunConfHandler;

import java.awt.*;
import java.io.File;

import static com.intellij.codeInsight.hint.HintUtil.ERROR_COLOR_KEY;
import static com.intellij.openapi.editor.colors.EditorColorsUtil.getGlobalOrDefaultColor;

abstract public class FileEvent extends Event {
    public static final String ID = "FileEvent";
    File file;

    FileEvent(Project project, PythonRunConfHandler handler,  String[] args) {
        super(project, handler);
        this.file = new File(this.handler.convertPathToLocal(args[0]));
    }
}
