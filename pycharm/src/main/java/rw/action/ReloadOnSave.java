package rw.action;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import org.jetbrains.annotations.NotNull;


public class ReloadOnSave implements FileDocumentManagerListener {
    public ReloadOnSave() {
        super();
    }

    @Override
    public void beforeDocumentSaving(@NotNull Document document) {
        ManualReload.handleSave(null, new Document[]{document});
    }

}
