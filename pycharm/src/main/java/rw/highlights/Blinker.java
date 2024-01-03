package rw.highlights;

import org.jetbrains.annotations.VisibleForTesting;
import rw.preferences.Preferences;
import rw.preferences.PreferencesState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Blinker {
    @VisibleForTesting
    public static Blinker singleton;
    List<Blink> all;
    Thread cleaner;
    Lock lock;

    @VisibleForTesting
    public Blinker() {
        this.all = new ArrayList<>();
        this.cleaner = new Thread(this::cleanerTarget);
        this.cleaner.start();
        this.lock = new ReentrantLock();
    }

    public static Blinker get() {
        if (Blinker.singleton == null) {
            Blinker.singleton = new Blinker();
        }
        return Blinker.singleton;
    }

    public void blink(Blink blink) {
        PreferencesState state = Preferences.get().getState();

        if (state.blinkDuration == 0) {
            return;
        }

        for (Blink b : new ArrayList<>(this.all)) {
            if (b == null) {
                continue;
            }

            if (b.equals(blink)) {
                b.resetExpiration();
                return;
            }
        }

        try {
            this.lock.lock();
            blink.render();
            this.all.add(blink);
        }
        finally {
            this.lock.unlock();
        }

    }

    private void cleanerTarget() {
        while (true) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }

            try {
                this.lock.lock();
                for (Blink b : new ArrayList<>(this.all)) {
                    if (b == null) {
                        continue;
                    }

                    if (b.isExpired()) {
                        b.remove();
                        this.all.remove(b);
                    }
                }
            }
            finally {
                this.lock.unlock();
            }
        }
    }
}
