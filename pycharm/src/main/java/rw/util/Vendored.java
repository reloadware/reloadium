package rw.util;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.ConfigurationFromContext;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Vendored {
    public static @Nullable RunnerAndConfigurationSettings getRunConfigForCurrentFile(@NotNull PsiFile psiFile) {
        // The 'Run current file' feature doesn't depend on the caret position in the file, that's why ConfigurationContext is created like this.
        ConfigurationContext configurationContext = new ConfigurationContext(psiFile);

        // The 'Run current file' feature doesn't reuse existing run configurations (by design).
        List<ConfigurationFromContext> configurationsFromContext = configurationContext.createConfigurationsFromContext();

        List<RunnerAndConfigurationSettings> runConfigs =
                configurationsFromContext != null
                        ? ContainerUtil.map(configurationsFromContext, ConfigurationFromContext::getConfigurationSettings)
                        : Collections.emptyList();

        VirtualFile vFile = psiFile.getVirtualFile();
        String filePath = vFile != null ? vFile.getPath() : null;
        for (RunnerAndConfigurationSettings config : runConfigs) {
            ((RunnerAndConfigurationSettingsImpl) config).setFilePathIfRunningCurrentFile(filePath);
        }

        if(runConfigs.isEmpty()){
            return null;
        }

        return runConfigs.get(0);
    }
}
