package com.cynorra.lexio.mvi;

import androidx.lifecycle.ViewModel;

import com.cynorra.lexio.data.UserProgress;
import com.cynorra.lexio.model.DailyQuizQuestion;

import java.util.List;

/**
 * Keeps the {@link AppStore} alive across configuration changes (rotation, theme switch,
 * locale change) so the user isn't sent back to the landing screen mid-simulation.
 */
public class StoreHolder extends ViewModel {
    public final AppStore store = new AppStore();
    /** True once the first Activity instance has restored the session and saved preferences. */
    public boolean initialized = false;
    /** True once the server copy of the user's progress was read successfully. Until then we
     *  must not push local progress, or an unread (offline) server score could be overwritten. */
    public boolean remoteSynced = false;
    /** Last progress persisted to local cache / server, so unchanged state isn't re-written. */
    public UserProgress lastSynced = null;

    // Daily quiz content for the screen. Kept here (not in AppState) because it is bulky server
    // data, and it must survive recreation of the Activity.
    public List<DailyQuizQuestion> quiz = null;
    public String quizKey = null; // country|date|language the loaded quiz belongs to
    public boolean quizLoading = false;
    public boolean quizFailed = false;
}
