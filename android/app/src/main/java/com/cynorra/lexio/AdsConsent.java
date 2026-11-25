package com.cynorra.lexio;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.ads.MobileAds;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

/**
 * Google's User Messaging Platform flow. Ads are initialised and requested once consent allows it
 * or when UMP is not configured/times out outside the EEA/UK.
 */
public final class AdsConsent {
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
    private static boolean sdkStarted;
    private static volatile boolean canRequestAds;

    private AdsConsent() {}

    public static boolean canRequestAds() {
        return canRequestAds;
    }

    /** Runs the consent flow; {@code onReady} is called (on the main thread) once ads may be requested. */
    public static void gather(Activity activity, Runnable onReady) {
        ConsentInformation info = UserMessagingPlatform.getConsentInformation(activity);
        info.requestConsentInfoUpdate(
                activity,
                new ConsentRequestParameters.Builder().build(),
                () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity, formError -> proceed(activity, onReady)),
                requestError -> proceed(activity, onReady));
        // A previous session may already have consent: ads can start right away without waiting for the network.
        if (info.canRequestAds()) {
            proceed(activity, onReady);
        } else {
            // Fallback timeout in case UMP network request hangs
            MAIN_HANDLER.postDelayed(() -> proceed(activity, onReady), 4000L);
        }
    }

    private static void proceed(Context context, Runnable onReady) {
        boolean first = !canRequestAds;
        canRequestAds = true;
        if (!sdkStarted) {
            sdkStarted = true;
            MobileAds.initialize(context.getApplicationContext(), status -> {
                MAIN_HANDLER.post(onReady);
            });
            return;
        }
        if (first) {
            MAIN_HANDLER.post(onReady);
        }
    }

    /** True in regions where the user must be able to reopen the consent choices (EEA/UK). */
    public static boolean privacyOptionsRequired(Activity activity) {
        return UserMessagingPlatform.getConsentInformation(activity).getPrivacyOptionsRequirementStatus()
                == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED;
    }

    public static void showPrivacyOptions(Activity activity) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, formError -> { });
    }
}
