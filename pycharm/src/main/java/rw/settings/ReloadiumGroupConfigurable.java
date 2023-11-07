package rw.settings;

import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.options.ex.SortedConfigurableGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.configurable.VcsMappingConfigurable;
import org.jetbrains.annotations.NotNull;
import rw.consts.Const;
import rw.lang.RwBundle;

public class ReloadiumGroupConfigurable extends SortedConfigurableGroup implements SearchableConfigurable.Parent {
    private static final String ID = "rw.settings.ReloadiumGroupConfigurable";
    Project project;

    private static final int GROUP_WEIGHT = 45;

    public ReloadiumGroupConfigurable(@NotNull Project project) {
        super(ID,
          StringUtil.capitalize(Const.get().packageName),
          RwBundle.message("configurable.description"),
          VcsMappingConfigurable.HELP_ID,
          GROUP_WEIGHT);
        this.project = project;
    }
}
