package rw.media;

import javax.swing.*;
import java.io.IOException;

/*
 * https://www.jetbrains.org/intellij/sdk/docs/reference_guide/work_with_icons_and_images.html
 */
public class Media {
    public static Icon RunExample;
    public static Icon DebugExample;
    public static Icon FixingUserErrors;
    public static Icon FixingFrameErrors;

    static {
        try {
            RunExample = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/example_run.gif").readAllBytes());
            DebugExample = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/example_debug.gif").readAllBytes());
            FixingUserErrors = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/fixing_user_errors.gif").readAllBytes());
            FixingFrameErrors = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/fixing_frame_errors.gif").readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
