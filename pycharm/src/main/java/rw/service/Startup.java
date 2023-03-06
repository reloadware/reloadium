package rw.service;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

public class Startup implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        Service.get();
    }
}
