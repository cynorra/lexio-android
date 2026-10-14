package com.cynorra.lexio;

import android.app.Application;
import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.cynorra.lexio.service.DailyQuizReminderWorker;
import com.cynorra.lexio.service.LexioNotificationManager;
import com.cynorra.lexio.service.NotificationSettings;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class LexioApplication extends Application {
    // v2: the first version fired 24h after install (a random time of day). v2 fires at a fixed
    // local time, and the old schedule is cancelled so existing installs move over.
    private static final String DAILY_QUIZ_REMINDER_WORK = "daily_quiz_reminder_v2";
    private static final String LEGACY_REMINDER_WORK = "daily_quiz_reminder";

    @Override
    public void onCreate() {
        super.onCreate();
        // The AdMob SDK starts after the consent flow (see AdsConsent), not here.

        // Channels must exist before a background FCM message arrives, or Android files it under "Miscellaneous".
        new LexioNotificationManager(this);
        refreshReminder(this);
    }

    /**
     * (Re)schedules the daily reminder from the user's settings. WorkManager persists the schedule, so it fires
     * with the app closed and after a reboot; the work is replaced only when the chosen hour actually changed.
     */
    public static void refreshReminder(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelUniqueWork(LEGACY_REMINDER_WORK);
        if (!NotificationSettings.isEnabled(context)) {
            workManager.cancelUniqueWork(DAILY_QUIZ_REMINDER_WORK);
            NotificationSettings.setScheduledHour(context, -1);
            return;
        }
        int hour = NotificationSettings.reminderHour(context);
        boolean changed = NotificationSettings.scheduledHour(context) != hour;
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                DailyQuizReminderWorker.class, 1, TimeUnit.DAYS
        ).setInitialDelay(millisUntilNext(hour), TimeUnit.MILLISECONDS).build();
        workManager.enqueueUniquePeriodicWork(
                DAILY_QUIZ_REMINDER_WORK,
                changed ? ExistingPeriodicWorkPolicy.UPDATE : ExistingPeriodicWorkPolicy.KEEP,
                request
        );
        NotificationSettings.setScheduledHour(context, hour);
    }

    private static long millisUntilNext(int hour) {
        Calendar now = Calendar.getInstance();
        Calendar next = (Calendar) now.clone();
        next.set(Calendar.HOUR_OF_DAY, hour);
        next.set(Calendar.MINUTE, 0);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        if (!next.after(now)) {
            next.add(Calendar.DAY_OF_MONTH, 1);
        }
        return next.getTimeInMillis() - now.getTimeInMillis();
    }
}
