package rw.highlights;

import org.jetbrains.annotations.VisibleForTesting;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;

import java.util.ArrayList;
import java.util.List;


public class Blinker {
    @VisibleForTesting
    public static Blinker singleton;
    List<Blink> all;
    Thread cleaner;


    @VisibleForTesting
    public Blinker() {
        this.all = new ArrayList<>();
        this.cleaner = new Thread(this::cleanerTarget);
        this.cleaner.start();
    }

    public static Blinker get() {
        if (Blinker.singleton == null) {
            Blinker.singleton = new Blinker();
        }
        return Blinker.singleton;
    }

    public void blink(Blink blink) {
        PreferencesState state = Preferences.getInstance().getState();

        if (state.blinkDuration == 0) {
            return;
        }

        for (Blink b : this.all) {
            if (b.equals(blink)) {
                b.resetExpiration();
                return;
            }
        }

        blink.render();
        this.all.add(blink);
    }

    private void cleanerTarget() {
        while (true) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
            for (Blink b : new ArrayList<>(this.all)) {
                if (b.isExpired()) {
                    b.remove();
                    this.all.remove(b);
                }
            }
        }
    }
}
