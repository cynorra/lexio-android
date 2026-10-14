package com.cynorra.lexio;

/**
 * Switches for features that exist in the code but have no content yet. The community tabs (feed, study
 * groups) read Firestore collections that are empty, so showing them only makes the app look unfinished;
 * flip this to true once there is real content and users to fill them.
 */
public final class Features {
    private Features() {}

    public static final boolean COMMUNITY_TABS = false;
}
