package com.cynorra.lexio;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.ads.MobileAds;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

/**
 * Google's User Messaging Platform flow. Ads are initialised and requested only once consent allows it
 * (always the case outside the EEA/UK unless the user withdrew it). The consent form is configured in the
 * AdMob console under Privacy & messaging.
 */
public final class AdsConsent {
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
                () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity, formError -> proceed(activity, info, onReady)),
                requestError -> proceed(activity, info, onReady));
        // A previous session may already have consent: ads can start right away without waiting for the network.
        if (info.canRequestAds()) proceed(activity, info, onReady);
    }

    private static void proceed(Context context, ConsentInformation info, Runnable onReady) {
        if (!info.canRequestAds()) return;
        boolean first = !canRequestAds;
        canRequestAds = true;
        if (!sdkStarted) {
            sdkStarted = true;
            MobileAds.initialize(context.getApplicationContext(), status -> { });
        }
        if (first) onReady.run();
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
