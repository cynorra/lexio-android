package com.cynorra.lexio.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Runs once a day via WorkManager (scheduled in LexioApplication) to remind users who
 * haven't opened the app to keep their daily streak going, even while the app is closed.
 */
public class DailyQuizReminderWorker extends Worker {
    public DailyQuizReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        new LexioNotificationManager(getApplicationContext()).triggerDailyQuizPushNotification();
        return Result.success();
    }
}
