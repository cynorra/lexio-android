package com.cynorra.lexio.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cynorra.lexio.MainActivity;
import com.cynorra.lexio.R;

import java.util.Locale;

public class LexioNotificationManager {
    public static final String CHANNEL_DAILY_QUIZ = "channel_daily_quiz";
    private static final int NOTIFICATION_DAILY_QUIZ = 1001;
    /** Intent extra that makes MainActivity open the daily quiz (once signed in). */
    public static final String EXTRA_OPEN_DAILY_QUIZ = "open_daily_quiz";

    /** Same preference file/key MainActivity writes the user's language choice to. */
    private static final String PREFS_NAME = "lexio_prefs";
    private static final String PREF_LANGUAGE = "language";

    private final Context context;

    public LexioNotificationManager(Context context) {
        // Notification text must follow the in-app language choice, which (unlike the
        // Activity's resources) the application context doesn't know about.
        this.context = localized(context.getApplicationContext());
        createNotificationChannels();
    }

    private static Context localized(Context base) {
        SharedPreferences prefs = base.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String language = prefs.getString(PREF_LANGUAGE, null);
        if (language == null) return base;
        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.setLocale(new Locale(language));
        return base.createConfigurationContext(config);
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel quizChannel = new NotificationChannel(
                    CHANNEL_DAILY_QUIZ,
                    context.getString(R.string.notif_channel_quiz_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            quizChannel.setDescription(context.getString(R.string.notif_channel_quiz_desc));

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(quizChannel);
            }
        }
    }

    /**
     * Sends the daily quiz streak reminder. Scheduled once a day by
     * {@link DailyQuizReminderWorker} via WorkManager (see LexioApplication) — not meant to be
     * called on app open. Safe to call with no running Activity.
     */
    public void triggerDailyQuizPushNotification() {
        // Opens the app straight on the daily quiz. No CLEAR_TASK: if the app is already open, its
        // state is kept and MainActivity (singleTop) just receives the new intent.
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(EXTRA_OPEN_DAILY_QUIZ, true);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String message = context.getString(R.string.notif_quiz_body);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_DAILY_QUIZ)
                .setSmallIcon(R.drawable.ic_stat_lexio)
                .setColor(ContextCompat.getColor(context, R.color.accent_primary))
                .setContentTitle(context.getString(R.string.notif_quiz_title))
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_DAILY_QUIZ, builder.build());
        } catch (SecurityException e) {
            // POST_NOTIFICATIONS was denied on Android 13+; nothing to show.
        }
    }
}
