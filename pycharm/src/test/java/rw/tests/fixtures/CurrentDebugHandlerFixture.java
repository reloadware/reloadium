package rw.tests.fixtures;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import rw.handler.RunConfHandler;
import rw.handler.RunConfHandlerManager;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;


public class CurrentDebugHandlerFixture {
    private final Project project;
    RunConfHandler runConfHandler;

    public CurrentDebugHandlerFixture(@NotNull Project project, @NotNull RunConfHandler runConfHandler) {
        this.project = project;
        this.runConfHandler = runConfHandler;
    }

    public void setUp() {
        RunConfHandlerManager runConfHandlerManager = mock(RunConfHandlerManager.class);
        lenient().doReturn(this.runConfHandler).when(runConfHandlerManager).getCurrentDebugHandler(this.project);
        RunConfHandlerManager.singleton = runConfHandlerManager;
    }

    public void tearDown() {
    }
}
