package rw.pkg.wheel;

import org.jetbrains.annotations.Nullable;


abstract public class WheelFactory {
    public static BaseWheel factory(String input) {
        if (input.contains("linux")) {
            return new UnixWheel(input);
        }
        else if (input.contains("macos")) {
            return new MacWheel(input);
        } else if (input.contains("win")) {
            return new WinWheel(input);
        } else if (input.contains("none-any")) {
            return new NoneAnyWheel(input);
        } else if (input.contains("tar.gz")) {
            return new TarWheel(input);
        } else {
            throw new RuntimeException("Unknown Os type");
        }
    }
}
