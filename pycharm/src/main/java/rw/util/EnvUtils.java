package rw.util;

public class EnvUtils {
    public static String boolToEnv(boolean value) {
        if (value) {
            return "True";
        } else {
            return "False";
        }
    }
}
