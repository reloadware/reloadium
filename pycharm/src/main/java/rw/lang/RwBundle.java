package rw.lang;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public final class RwBundle extends DynamicBundle {
    @NonNls
    public static final String BUNDLE = "messages.RwBundle";
    private static final RwBundle INSTANCE = new RwBundle();

    private RwBundle() {
        super(BUNDLE);
    }

    @NotNull
    public static @Nls String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object @NotNull ... params) {
        if (INSTANCE.containsKey(key)) {
            return INSTANCE.getMessage(key, params);
        }
        return INSTANCE.message(key, params);
    }
}