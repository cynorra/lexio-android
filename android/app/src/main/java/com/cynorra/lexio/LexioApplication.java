package com.cynorra.lexio;

import android.app.Application;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.cynorra.lexio.service.DailyQuizReminderWorker;
import com.google.android.gms.ads.MobileAds;

import java.util.concurrent.TimeUnit;

public class LexioApplication extends Application {
    private static final String DAILY_QUIZ_REMINDER_WORK = "daily_quiz_reminder";

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize AdMob SDK
        MobileAds.initialize(this, initializationStatus -> {});

        scheduleDailyQuizReminder();
    }

    private void scheduleDailyQuizReminder() {
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                DailyQuizReminderWorker.class, 1, TimeUnit.DAYS
        ).build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                DAILY_QUIZ_REMINDER_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                request
        );
    }
}
