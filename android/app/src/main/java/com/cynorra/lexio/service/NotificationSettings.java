package com.cynorra.lexio.service;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Everything about when and whether the app may notify the user. Kept in its own preference file (not
 * backed up: the WorkManager schedule and push topics must be recreated on a new device anyway).
 *
 * Rules that protect the user from notification fatigue: one reminder per day at most, never more than
 * {@link #WEEKLY_CAP} in any seven days, never after the quiz is done, and the user can switch it all off
 * or pick the hour.
 */
public final class NotificationSettings {
    private static final String PREFS = "lexio_notification_settings";
    private static final String KEY_ENABLED = "enabled";
    private static final String KEY_HOUR = "hour";               // -1 = automatic
    private static final String KEY_SCHEDULED_HOUR = "scheduled_hour";
    private static final String KEY_QUIZ_HOURS = "quiz_hours";   // hours of day of the last quiz completions
    private static final String KEY_SENT_DAYS = "sent_days";     // epoch days of recent reminders
    private static final String KEY_TOPICS = "push_topics";

    public static final int DEFAULT_HOUR = 19;
    public static final int MIN_HOUR = 8;
    public static final int MAX_HOUR = 22;
    public static final int WEEKLY_CAP = 5;
    private static final int HOURS_REMEMBERED = 7;
    private static final int MIN_SAMPLES_FOR_AUTO = 3;

    private NotificationSettings() {}

    private static SharedPreferences prefs(Context c) {
        return c.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static boolean isEnabled(Context c) {
        return prefs(c).getBoolean(KEY_ENABLED, true);
    }

    public static void setEnabled(Context c, boolean enabled) {
        prefs(c).edit().putBoolean(KEY_ENABLED, enabled).apply();
    }

    /** The hour the user picked, or -1 when the app chooses (automatic). */
    public static int manualHour(Context c) {
        return prefs(c).getInt(KEY_HOUR, -1);
    }

    public static void setManualHour(Context c, int hourOrMinusOne) {
        prefs(c).edit().putInt(KEY_HOUR, hourOrMinusOne).apply();
    }

    /** Hour of day (local time) the reminder should fire at. */
    public static int reminderHour(Context c) {
        return chooseHour(manualHour(c), quizHours(c));
    }

    /** The reminder hour for a manual choice (or -1) and the hours the user recently completed quizzes at. */
    static int chooseHour(int manual, List<Integer> recentQuizHours) {
        if (manual >= 0) return clamp(manual);
        if (recentQuizHours.size() < MIN_SAMPLES_FOR_AUTO) return DEFAULT_HOUR;
        int sum = 0;
        for (int h : recentQuizHours) sum += h;
        return clamp(Math.round(sum / (float) recentQuizHours.size()));
    }

    public static boolean isAutomatic(Context c) {
        return manualHour(c) < 0;
    }

    private static int clamp(int hour) {
        return Math.max(MIN_HOUR, Math.min(MAX_HOUR, hour));
    }

    /** Remembers when the user actually plays, so the automatic reminder lands at a time that suits them. */
    public static void recordQuizCompletion(Context c) {
        List<Integer> hours = quizHours(c);
        hours.add(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        while (hours.size() > HOURS_REMEMBERED) hours.remove(0);
        StringBuilder sb = new StringBuilder();
        for (int h : hours) sb.append(sb.length() == 0 ? "" : ",").append(h);
        prefs(c).edit().putString(KEY_QUIZ_HOURS, sb.toString()).apply();
    }

    private static List<Integer> quizHours(Context c) {
        List<Integer> out = new ArrayList<>();
        String raw = prefs(c).getString(KEY_QUIZ_HOURS, "");
        for (String part : raw.split(",")) {
            try {
                if (!part.isEmpty()) out.add(Integer.parseInt(part.trim()));
            } catch (NumberFormatException ignored) {
                // a corrupt entry is dropped
            }
        }
        return out;
    }

    public static int scheduledHour(Context c) {
        return prefs(c).getInt(KEY_SCHEDULED_HOUR, -1);
    }

    public static void setScheduledHour(Context c, int hour) {
        prefs(c).edit().putInt(KEY_SCHEDULED_HOUR, hour).apply();
    }

    // ---- frequency cap

    private static long today() {
        return System.currentTimeMillis() / 86_400_000L;
    }

    private static List<Long> recentSentDays(Context c) {
        List<Long> out = new ArrayList<>();
        for (String part : prefs(c).getString(KEY_SENT_DAYS, "").split(",")) {
            try {
                if (!part.isEmpty()) out.add(Long.parseLong(part.trim()));
            } catch (NumberFormatException ignored) {
                // dropped
            }
        }
        return out;
    }

    /** True if another reminder is allowed: not already sent today and under the weekly cap. */
    public static boolean canSendReminder(Context c) {
        return withinCap(recentSentDays(c), today());
    }

    /** One reminder per day at most, and fewer than {@link #WEEKLY_CAP} in the last seven days. */
    static boolean withinCap(List<Long> sentDays, long today) {
        int inLastWeek = 0;
        for (long day : sentDays) {
            if (day == today) return false;
            if (day > today - 7) inLastWeek++;
        }
        return inLastWeek < WEEKLY_CAP;
    }

    public static void recordReminderSent(Context c) {
        long today = today();
        StringBuilder sb = new StringBuilder();
        for (long day : recentSentDays(c)) {
            if (day > today - 7) sb.append(sb.length() == 0 ? "" : ",").append(day);
        }
        sb.append(sb.length() == 0 ? "" : ",").append(today);
        prefs(c).edit().putString(KEY_SENT_DAYS, sb.toString()).apply();
    }

    // ---- push topics currently subscribed on this device

    public static Set<String> pushTopics(Context c) {
        Set<String> out = new LinkedHashSet<>();
        for (String part : prefs(c).getString(KEY_TOPICS, "").split(",")) {
            if (!part.isEmpty()) out.add(part);
        }
        return out;
    }

    public static void setPushTopics(Context c, Set<String> topics) {
        StringBuilder sb = new StringBuilder();
        for (String t : topics) sb.append(sb.length() == 0 ? "" : ",").append(t);
        prefs(c).edit().putString(KEY_TOPICS, sb.toString()).apply();
    }
}
