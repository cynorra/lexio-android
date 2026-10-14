package com.cynorra.lexio;

import android.app.Application;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.cynorra.lexio.service.DailyQuizReminderWorker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class LexioApplication extends Application {
    // v2: the first version fired 24h after install (a random time of day). v2 fires at a fixed
    // local time, and the old schedule is cancelled so existing installs move over.
    private static final String DAILY_QUIZ_REMINDER_WORK = "daily_quiz_reminder_v2";
    private static final String LEGACY_REMINDER_WORK = "daily_quiz_reminder";
    private static final int REMINDER_HOUR = 19; // local time

    @Override
    public void onCreate() {
        super.onCreate();
        // The AdMob SDK starts after the consent flow (see AdsConsent), not here.

        scheduleDailyQuizReminder();
    }

    /** WorkManager persists the schedule, so it fires with the app closed and after a reboot. */
    private void scheduleDailyQuizReminder() {
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                DailyQuizReminderWorker.class, 1, TimeUnit.DAYS
        ).setInitialDelay(millisUntilNextReminder(), TimeUnit.MILLISECONDS).build();

        WorkManager workManager = WorkManager.getInstance(this);
        workManager.cancelUniqueWork(LEGACY_REMINDER_WORK);
        workManager.enqueueUniquePeriodicWork(
                DAILY_QUIZ_REMINDER_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                request
        );
    }

    private static long millisUntilNextReminder() {
        Calendar now = Calendar.getInstance();
        Calendar next = (Calendar) now.clone();
        next.set(Calendar.HOUR_OF_DAY, REMINDER_HOUR);
        next.set(Calendar.MINUTE, 0);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        if (!next.after(now)) {
            next.add(Calendar.DAY_OF_MONTH, 1);
        }
        return next.getTimeInMillis() - now.getTimeInMillis();
    }
}
