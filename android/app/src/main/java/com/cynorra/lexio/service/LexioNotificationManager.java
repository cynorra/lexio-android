package com.cynorra.lexio.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cynorra.lexio.MainActivity;
import com.cynorra.lexio.R;

public class LexioNotificationManager {
    public static final String CHANNEL_DAILY_QUIZ = "channel_daily_quiz";
    public static final String CHANNEL_ALERTS = "channel_legal_alerts";

    private final Context context;

    public LexioNotificationManager(Context context) {
        this.context = context;
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel 1: Daily Quiz & Streak Notifications
            NotificationChannel quizChannel = new NotificationChannel(
                    CHANNEL_DAILY_QUIZ,
                    "Günlük Hukuk Testi & Seri Bildirimleri",
                    NotificationManager.IMPORTANCE_HIGH
            );
            quizChannel.setDescription("Günün yeni testleri ve günlük streak hatırlatmaları");

            // Channel 2: Legal Alerts & Emsal Karar Warnings
            NotificationChannel alertChannel = new NotificationChannel(
                    CHANNEL_ALERTS,
                    "Hukuki Uyarılar & Yargıtay Gündemi",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            alertChannel.setDescription("Yeni emsal kararlar, mevzuat değişiklikleri ve uyarı mesajları");

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(quizChannel);
                manager.createNotificationChannel(alertChannel);
            }
        }
    }

    /**
     * Sends a System Push Tray Notification (Works even when app is CLOSED / Background)
     */
    public void sendBackgroundPushNotification(int notificationId, String title, String message, String channelId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(notificationId, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send Daily Quiz Reminder Push (App Closed / Background)
     */
    public void triggerDailyQuizPushNotification() {
        sendBackgroundPushNotification(
                1001,
                "🔥 Günün Hukuk Testi Hazır! (+100 Puan)",
                "Bugünün ÖSYM / HMGS uyumlu yeni soruları yayımlandı. Serini korumak için hemen testi çöz!",
                CHANNEL_DAILY_QUIZ
        );
    }

    /**
     * Send Legal Warning / Precedent Alert Push (App Closed / Background)
     */
    public void triggerLegalWarningPushNotification(String title, String details) {
        sendBackgroundPushNotification(
                1002,
                "⚖️ " + title,
                details,
                CHANNEL_ALERTS
        );
    }
}
