package rw.tests.fixtures;

import rw.util.NotificationManager;

import static org.mockito.Mockito.spy;


public class NotificationManagerFixture {
    private NotificationManager notificationManager;

    public NotificationManagerFixture() throws Exception {

    }

    public void start() {
        this.notificationManager = spy(new NotificationManager());
        NotificationManager.singleton = this.notificationManager;
    }

    public void stop() {
    }

    public NotificationManager getNotificationManager() {
        return this.notificationManager;
    }
}
