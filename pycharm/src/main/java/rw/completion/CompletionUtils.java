package rw.completion;

import com.intellij.icons.AllIcons;
import icons.PythonPsiApiIcons;

import javax.swing.*;
import java.util.Map;

import static java.util.Map.entry;

public class CompletionUtils {
    static Map<String, Icon> TYPE_TO_ICON = Map.ofEntries(
                    entry("module",PythonPsiApiIcons.PythonFile),
                    entry("function",AllIcons.Nodes.Function),
                    entry("builtin_function_or_method", AllIcons.Nodes.Function),
                    entry("method", AllIcons.Nodes.Method),
                    entry("method-wrapper", AllIcons.Nodes.Method),
                    entry("type", AllIcons.Nodes.Class),
                    entry("package", AllIcons.Nodes.Package)
            );
}
