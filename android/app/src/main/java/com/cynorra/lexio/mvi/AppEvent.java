package com.cynorra.lexio.mvi;

import com.cynorra.lexio.data.UserProgress;
import com.cynorra.lexio.model.Case;
import com.cynorra.lexio.model.StudyTopic;

/**
 * Events are created only through the named factories below. (An earlier version exposed
 * overloaded constructors taking a bare String, which silently routed role / interval
 * values into the wrong field.)
 */
public class AppEvent {
    public enum Type {
        NAVIGATE_TO_CASE_LIST,
        SELECT_CASE,
        CHOOSE_ROLE,
        START_SIMULATION,
        SELECT_CHOICE,
        NAVIGATE_TO_STUDY_PORTAL,
        SELECT_STUDY_TOPIC,
        NAVIGATE_TO_LEADERBOARD,
        SET_LEADERBOARD_INTERVAL,
        NAVIGATE_TO_FEED,
        NAVIGATE_TO_WORKSPACE,
        NAVIGATE_TO_PROFILE,
        TOGGLE_THEME,
        SET_DARK_THEME,
        TOGGLE_LANGUAGE,
        SET_LANGUAGE,
        NAVIGATE_TO_DAILY_QUIZ,
        QUIZ_ANSWER,
        QUIZ_NEXT,
        SELECT_COUNTRY,
        SET_INSTAGRAM_HANDLE,
        PROGRESS_LOADED,
        LOGIN_SUCCESS,
        LOGOUT
    }

    private final Type type;
    private Case selectedCase;
    private Case.Choice selectedChoice;
    private StudyTopic selectedStudyTopic;
    private String text; // role, interval, display name, language, country or handle, per type
    private boolean flag;
    private int number;
    private int total;
    private UserProgress progress;
    private String today;

    private AppEvent(Type type) {
        this.type = type;
    }

    public static AppEvent of(Type type) {
        return new AppEvent(type);
    }

    public static AppEvent loginSuccess(String displayName) {
        AppEvent e = new AppEvent(Type.LOGIN_SUCCESS);
        e.text = displayName;
        return e;
    }

    public static AppEvent selectCase(Case c) {
        AppEvent e = new AppEvent(Type.SELECT_CASE);
        e.selectedCase = c;
        return e;
    }

    public static AppEvent chooseRole(String role) {
        AppEvent e = new AppEvent(Type.CHOOSE_ROLE);
        e.text = role;
        return e;
    }

    public static AppEvent selectChoice(Case.Choice choice) {
        AppEvent e = new AppEvent(Type.SELECT_CHOICE);
        e.selectedChoice = choice;
        return e;
    }

    public static AppEvent selectStudyTopic(StudyTopic topic) {
        AppEvent e = new AppEvent(Type.SELECT_STUDY_TOPIC);
        e.selectedStudyTopic = topic;
        return e;
    }

    public static AppEvent setLeaderboardInterval(String interval) {
        AppEvent e = new AppEvent(Type.SET_LEADERBOARD_INTERVAL);
        e.text = interval;
        return e;
    }

    public static AppEvent setLanguage(String language) {
        AppEvent e = new AppEvent(Type.SET_LANGUAGE);
        e.text = language;
        return e;
    }

    public static AppEvent setDarkTheme(boolean dark) {
        AppEvent e = new AppEvent(Type.SET_DARK_THEME);
        e.flag = dark;
        return e;
    }

    public static AppEvent selectCountry(String countryCode) {
        AppEvent e = new AppEvent(Type.SELECT_COUNTRY);
        e.text = countryCode;
        return e;
    }

    public static AppEvent setInstagramHandle(String handle) {
        AppEvent e = new AppEvent(Type.SET_INSTAGRAM_HANDLE);
        e.text = handle;
        return e;
    }

    /** @param today yyyy-MM-dd, used to expire a streak that was not continued. */
    public static AppEvent progressLoaded(UserProgress progress, String today) {
        AppEvent e = new AppEvent(Type.PROGRESS_LOADED);
        e.progress = progress;
        e.today = today;
        return e;
    }

    public static AppEvent quizAnswer(int optionIndex, boolean correct) {
        AppEvent e = new AppEvent(Type.QUIZ_ANSWER);
        e.number = optionIndex;
        e.flag = correct;
        return e;
    }

    /** @param totalQuestions size of the quiz; @param today yyyy-MM-dd of completion. */
    public static AppEvent quizNext(int totalQuestions, String today) {
        AppEvent e = new AppEvent(Type.QUIZ_NEXT);
        e.total = totalQuestions;
        e.today = today;
        return e;
    }

    public Type getType() { return type; }
    public Case getSelectedCase() { return selectedCase; }
    public Case.Choice getSelectedChoice() { return selectedChoice; }
    public StudyTopic getSelectedStudyTopic() { return selectedStudyTopic; }
    public String getText() { return text; }
    public boolean getFlag() { return flag; }
    public int getNumber() { return number; }
    public int getTotal() { return total; }
    public UserProgress getProgress() { return progress; }
    public String getToday() { return today; }
}
