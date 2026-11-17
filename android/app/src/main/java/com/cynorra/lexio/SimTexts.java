package com.cynorra.lexio;

import java.util.Locale;

/**
 * Looks up the trial-dialogue labels in the language of the case being played (Turkish or English),
 * which is independent of the app's UI language. The pure-Java reducer has no Context, so the Activity
 * installs the resolver; unit tests run without one and get an empty string.
 */
public final class SimTexts {
    public interface Resolver {
        String get(int resId, boolean turkish);
    }

    private static volatile Resolver resolver = (resId, turkish) -> "";

    private SimTexts() {}

    public static void install(Resolver r) {
        resolver = r != null ? r : (resId, turkish) -> "";
    }

    public static String get(int resId, boolean turkish) {
        return resolver.get(resId, turkish);
    }

    public static String get(int resId, boolean turkish, Object... args) {
        String pattern = resolver.get(resId, turkish);
        return pattern.isEmpty() ? "" : String.format(turkish ? new Locale("tr") : Locale.ENGLISH, pattern, args);
    }
}
