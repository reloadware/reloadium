package rw.media;

import rw.audit.RwSentry;

import javax.swing.*;
import java.io.IOException;

public class Media {
    public static Icon RunExample;
    public static Icon DebugExample;
    public static Icon FixingUserErrors;
    public static Icon FixingFrameErrors;
    public static Icon HelpNeeded;
    public static ImageIcon inferno;
    public static ImageIcon plasma;
    public static ImageIcon viridis;

    static {
        try {
            RunExample = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/example_run.gif").readAllBytes());
            DebugExample = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/example_debug.gif").readAllBytes());
            FixingUserErrors = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/fixing_user_errors.gif").readAllBytes());
            FixingFrameErrors = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/fixing_frame_errors.gif").readAllBytes());
            HelpNeeded = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/help_needed.png").readAllBytes());
            inferno = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/inferno.png").readAllBytes());
            plasma = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/plasma.png").readAllBytes());
            viridis = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/viridis.png").readAllBytes());
        } catch (IOException e) {
            RwSentry.get().captureException(e, true);
        }
    }
}
