package com.cynorra.lexio;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Thin wrapper over Firebase Analytics. Only coarse product events (which screen, which feature was used)
 * are logged, never content the user typed. Failures are swallowed: analytics must never crash the app.
 */
public final class Telemetry {
    private static FirebaseAnalytics analytics;

    private Telemetry() {}

    public static void init(Context context) {
        try {
            analytics = FirebaseAnalytics.getInstance(context.getApplicationContext());
        } catch (RuntimeException ignored) {
            analytics = null;
        }
    }

    public static void screen(String name) {
        Bundle b = new Bundle();
        b.putString(FirebaseAnalytics.Param.SCREEN_NAME, name);
        log(FirebaseAnalytics.Event.SCREEN_VIEW, b);
    }

    public static void event(String name, String key, String value) {
        Bundle b = new Bundle();
        if (key != null) b.putString(key, value);
        log(name, b);
    }

    public static void event(String name, String key, long value) {
        Bundle b = new Bundle();
        if (key != null) b.putLong(key, value);
        log(name, b);
    }

    private static void log(String name, Bundle params) {
        try {
            if (analytics != null) analytics.logEvent(name, params);
        } catch (RuntimeException ignored) {
            // never let telemetry affect the user
        }
    }
}
