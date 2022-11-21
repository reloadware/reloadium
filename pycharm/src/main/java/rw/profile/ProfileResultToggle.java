package rw.profile;

import com.intellij.execution.ExecutionManager;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionEnvironmentProvider;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import org.jetbrains.annotations.NotNull;
import rw.handler.runConf.BaseRunConfHandler;
import rw.service.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class ProfileResultToggle extends ToggleAction implements DumbAware {
    public static final ExtensionPointName<com.intellij.openapi.vcs.actions.AnnotateToggleAction.Provider> EP_NAME =
            new ExtensionPointName<>("com.intellij.openapi.vcs.actions.AnnotateToggleAction.Provider");

    Map<Editor, ProfileGutterProvider> editorToGutter;

    public ProfileResultToggle() {
        setEnabledInModalContext(true);

        this.editorToGutter = new HashMap<>();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        Presentation presentation = e.getPresentation();
        presentation.setText("Profiling Details");
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        ProfileGutterProvider gutter = this.editorToGutter.getOrDefault(editor, null);

        if (gutter == null) {
            return false;
        }

        return gutter.isSelected();
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean selected) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        if (editor == null) {
            return;
        }

        this.editorToGutter.putIfAbsent(editor, new ProfileGutterProvider(editor));
        ProfileGutterProvider gutterProvider = this.editorToGutter.get(editor);

        if (selected){
          editor.getGutter().registerTextAnnotation(gutterProvider);
        }
        else {
          editor.getGutter().closeTextAnnotations(List.of(gutterProvider));
        }

        gutterProvider.setSelected(selected);
    }
}
