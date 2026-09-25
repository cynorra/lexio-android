package com.cynorra.lexio.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.cynorra.lexio.data.DateKeys;
import com.cynorra.lexio.data.ProgressRepository;

/**
 * Runs once a day via WorkManager (scheduled in LexioApplication) to remind users to keep
 * their daily streak going, even while the app is closed. Skipped if today's quiz is done.
 */
public class DailyQuizReminderWorker extends Worker {
    public DailyQuizReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        String lastQuizDate = context.getSharedPreferences("lexio_progress", Context.MODE_PRIVATE)
                .getString(ProgressRepository.KEY_LAST_QUIZ_DATE, null);
        if (!DateKeys.today().equals(lastQuizDate)) {
            new LexioNotificationManager(context).triggerDailyQuizPushNotification();
        }
        return Result.success();
    }
}
