package rw.completion;

import com.intellij.codeInsight.daemon.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.jetbrains.python.debugger.*;
import com.jetbrains.python.psi.PyFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rw.handler.BaseRunConfHandler;
import rw.handler.RunConfHandlerManager;
import rw.icons.Icons;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;

import java.util.*;


class FramePosition {
    String name;
    PySourcePosition position;

    FramePosition(String name, PySourcePosition position) {
        this.name = name;
        this.position = position;
    }
}


public class CompletableFunctionsLineMarker extends LineMarkerProviderDescriptor {
    @Override
    public @Nullable("null means disabled")
    @GutterName String getName() {
        return "Completable function";
    }

    public String getTooltip(@NotNull PyFunction element) {
        return String.format("Function has dynamic completions available", element.getName());
    }

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        return null;
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> elements, @NotNull Collection<? super LineMarkerInfo<?>> result) {
        PreferencesState preferences = Preferences.getInstance().getState();
        if (!preferences.runtimeCompletion) {
            return;
        }

        if (elements.isEmpty()) {
            return;
        }

        BaseRunConfHandler handler = RunConfHandlerManager.get().getCurrentHandler(elements.get(0).getProject());

        if (handler == null) {
            return;
        }

        XDebugSessionImpl debugSession = handler.getDebugSession();
        if (debugSession == null) {
            return;
        }

        Map<String, List<FramePosition>> fileToPositions = new HashMap<>();

        for (PyThreadInfo t : ((PyDebugProcess) debugSession.getDebugProcess()).getThreads()) {
            if (t.getFrames() == null) {
                continue;
            }

            for (PyStackFrameInfo f : t.getFrames()) {
                fileToPositions.putIfAbsent(f.getPosition().getFile(), new ArrayList<>());
                List<FramePosition> positions = fileToPositions.get(f.getPosition().getFile());

                positions.add(new FramePosition(t.getName(), f.getPosition()));
            }
        }

        for (PsiElement element : elements) {
            if (!(element instanceof PyFunction)) {
                continue;
            }

            VirtualFile file = element.getContainingFile().getOriginalFile().getVirtualFile();
            Document document = FileDocumentManager.getInstance().getDocument(file);

            if (document == null) {
                continue;
            }

            String path = file.getPath();

            PsiElement identifier = ((PyFunction) element).getIdentifyingElement();
            if (identifier == null) {
                continue;
            }

            List<FramePosition> framePositions = fileToPositions.get(path);

            if (framePositions == null) {
                continue;
            }

            for (FramePosition p : framePositions) {
                int startOffset = 0;
                try {
                    startOffset = document.getLineStartOffset(p.position.getLine() - 1);
                } catch (IndexOutOfBoundsException ignored) {
                    continue;
                }

                if (!element.getTextRange().contains(startOffset + 1)) {
                    continue;
                }
                LineMarkerInfo<PsiElement> marker = new LineMarkerInfo<>(identifier, identifier.getTextRange(), Icons.CompletableFunction, e -> this.getTooltip((PyFunction) element),
                        null, GutterIconRenderer.Alignment.LEFT);

                result.add(marker);
            }
        }

        super.collectSlowLineMarkers(elements, result);
    }
}