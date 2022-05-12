package rw.highlights;

import org.jetbrains.annotations.VisibleForTesting;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;

import java.util.ArrayList;
import java.util.List;


public class Blinker {
    List<Blink> all;

    @VisibleForTesting
    public static Blinker singleton;

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

        for (Blink b: this.all) {
            if (b.equals(blink)) {
                b.resetExpiration();
                return;
            }
        }

        blink.render();
        this.all.add(blink);
    }

    private void cleanerTarget() {
        PreferencesState state = Preferences.getInstance().getState();

        while (true) {
            try {
                Thread.sleep((long) (state.blinkDuration / 3.0));
            } catch (InterruptedException e) {
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
