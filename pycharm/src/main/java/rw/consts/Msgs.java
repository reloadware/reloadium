package rw.consts;

import com.intellij.openapi.util.text.StringUtil;

public class Msgs {
    public final String MISSING_CWD = "Working directory is not set";
    public final String EDIT_CONFIGURATION = "Edit configuration";
    public final String INSTALLING_PACKAGE;
    public final String UPDATED_SUCCESSFULLY;
    public final String ALREADY_UP_TO_DATE = "Already up to date";
    public final String INSTALLING_FAILED = "Installing packages failed";
    public final String CONFIG_NOT_SUPPORTED = "Selected configuration is not supported";

    Msgs(String packageName) {
        this.INSTALLING_PACKAGE = String.format("Installing %s package...", packageName);
        this.UPDATED_SUCCESSFULLY = String.format("%s package updated successfully", StringUtil.capitalize(packageName));
    }
}
