package com.example.pedometertest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class PedometerService extends Service {
    private static final String CHANNEL_ID = "PedometerChannel";
    private int steps = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            createNotificationChannel();
            startForeground(1, createNotification(0, 0)); // Initial notification
        } catch (Exception e) {
            e.printStackTrace(); // Log any exceptions
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(() -> {
            try {
                while (true) {
                    steps++;
                    Notification notification = createNotification(steps, steps * 0.0008); // Example conversion
                    NotificationManager manager = getSystemService(NotificationManager.class);
                    if (manager != null) {
                        manager.notify(1, notification); // Update notification
                    }
                    SystemClock.sleep(1000); // Update every second
                }
            } catch (Exception e) {
                e.printStackTrace(); // Log any exceptions
                stopSelf(); // Stop service if an error occurs
            }
        }).start();
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Pedometer Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Notification for Pedometer Service");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification(int steps, double distance) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Pedometer Running")
                .setContentText("Steps: " + steps + ", Distance: " + String.format("%.2f", distance) + " km")
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Temporary built-in icon
                .setPriority(NotificationCompat.PRIORITY_LOW) // Low priority for background tasks
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
