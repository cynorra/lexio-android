package com.cynorra.lexio.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** Local-calendar date keys used for the daily streak and the period leaderboards. */
public final class DateKeys {
    private DateKeys() {}

    private static SimpleDateFormat dayFormat() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    }

    public static String today() {
        return dayFormat().format(new Date());
    }

    /** The calendar day before {@code day} (yyyy-MM-dd), or null if it can't be parsed. */
    public static String previousDay(String day) {
        if (day == null) return null;
        try {
            Date d = dayFormat().parse(day);
            if (d == null) return null;
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            c.add(Calendar.DAY_OF_MONTH, -1);
            return dayFormat().format(c.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    public static String dailyKey() {
        return "daily_" + today();
    }

    public static String weeklyKey() {
        Calendar c = Calendar.getInstance(Locale.GERMANY); // ISO-8601 weeks: Monday start
        c.setMinimalDaysInFirstWeek(4);
        c.setFirstDayOfWeek(Calendar.MONDAY);
        // getWeekYear() handles the days around New Year that belong to the adjacent year's week.
        return String.format(Locale.US, "weekly_%d-W%02d", c.getWeekYear(), c.get(Calendar.WEEK_OF_YEAR));
    }

    public static String monthlyKey() {
        return new SimpleDateFormat("'monthly_'yyyy-MM", Locale.US).format(new Date());
    }

    public static String keyForInterval(String interval) {
        if ("weekly".equals(interval)) return weeklyKey();
        if ("monthly".equals(interval)) return monthlyKey();
        return dailyKey();
    }
}
