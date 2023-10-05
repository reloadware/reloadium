package rw.media;

import rw.audit.RwSentry;

import javax.swing.*;
import java.io.IOException;

public class Media {
    public static ImageIcon RunExample;
    public static ImageIcon DebugExample;
    public static ImageIcon FixingUserErrors;
    public static ImageIcon FixingFrameErrors;
    public static ImageIcon HelpNeeded;
    public static ImageIcon Logo;
    public static ImageIcon Inferno;
    public static ImageIcon Plasma;
    public static ImageIcon Viridis;

    static {
        try {
            RunExample = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/example_run.gif").readAllBytes());
            DebugExample = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/example_debug.gif").readAllBytes());
            FixingUserErrors = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/fixing_user_errors.gif").readAllBytes());
            FixingFrameErrors = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/fixing_frame_errors.gif").readAllBytes());
            HelpNeeded = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/help_needed.png").readAllBytes());
            Inferno = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/inferno.png").readAllBytes());
            Plasma = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/plasma.png").readAllBytes());
            Viridis = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/viridis.png").readAllBytes());
            Logo = new ImageIcon(Media.class.getClassLoader().getResourceAsStream("META-INF/media/logo.png").readAllBytes());
        } catch (IOException e) {
            RwSentry.get().captureException(e, true);
        }
    }
}
