package rw.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NewUI {
    public static boolean isEnabled() {
        try {
            Class<?> cls = Class.forName("com.intellij.ui.ExperimentalUI");
            Method method = cls.getDeclaredMethod("isNewUI");
            Boolean result = (Boolean) method.invoke(null);
            return result;
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return false;
        }
    }
}
