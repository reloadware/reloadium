package rw.debugger;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import rw.handler.Activable;
import rw.highlights.Highlighter;
import rw.session.events.LinesTraced;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FastDebug implements Activable {
    List<Highlighter> highlighters;
    Project project;

    Color TRACED_COLOR = new Color(0, 178, 44, 15);

    public FastDebug(Project project) {
        this.highlighters = new ArrayList<>();
        this.project = project;
    }

    public void onLinesTracedEvent(LinesTraced event) {
        Highlighter highlighter = new Highlighter(this.project,
                event.getFile(),
                event.getLineStart(),
                event.getLineEnd(),
                this.TRACED_COLOR,
                -10000,
                false);

        highlighter.show();
        this.highlighters.add(highlighter);
    }

    @Override
    public void activate() {
        ApplicationManager.getApplication().invokeLater(() -> this.highlighters.forEach(Highlighter::show));
    }

    @Override
    public void deactivate() {
        ApplicationManager.getApplication().invokeLater(() -> this.highlighters.forEach(Highlighter::hide));
    }
}
