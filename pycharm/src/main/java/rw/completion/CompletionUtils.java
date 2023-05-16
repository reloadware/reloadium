package rw.completion;

import com.intellij.icons.AllIcons;
import icons.PythonPsiApiIcons;

import javax.swing.*;
import java.util.Map;

import static java.util.Map.entry;

public class CompletionUtils {
    static Map<ObjectType, Icon> TYPE_TO_ICON = Map.ofEntries(
            entry(ObjectType.MODULE, PythonPsiApiIcons.PythonFile),
            entry(ObjectType.FUNCTION, AllIcons.Nodes.Function),
            entry(ObjectType.BUILTIN_FUN, AllIcons.Nodes.Function),
            entry(ObjectType.METHOD, AllIcons.Nodes.Method),
            entry(ObjectType.METHOD_WRAPPER, AllIcons.Nodes.Method),
            entry(ObjectType.TYPE, AllIcons.Nodes.Class),
            entry(ObjectType.PACKAGE, AllIcons.Nodes.Package)
    );
}
