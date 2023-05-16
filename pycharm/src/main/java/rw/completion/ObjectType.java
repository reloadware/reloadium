package rw.completion;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum ObjectType {
    MODULE("module"),
    FUNCTION("function"),
    BUILTIN_FUN("builtin_fun"),
    METHOD("method"),
    METHOD_WRAPPER("method-wrapper"),
    TYPE("type"),
    PACKAGE("package"),
    Any("any");

    static public final List<ObjectType> callableTypes = List.of(new ObjectType[]{FUNCTION, BUILTIN_FUN, METHOD_WRAPPER, METHOD});
    static public final Map<String, ObjectType> nameToType = Arrays.stream(ObjectType.values()).collect(Collectors.toMap(o -> o.name, o -> o));
    final String name;

    ObjectType(String name) {
        this.name = name;
    }

    String getName() {
        return this.name;
    }
}
