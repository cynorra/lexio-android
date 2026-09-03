package com.cynorra.lexio.mvi;

import com.cynorra.lexio.model.Case;
import com.cynorra.lexio.model.StudyTopic;
import java.util.ArrayList;
import java.util.List;

public class AppState {
    public enum Screen {
        LOGIN,
        LANDING,
        CASE_LIST,
        CASE_DETAIL,
        SIMULATION,
        LEADERBOARD,
        STUDY_PORTAL,
        STUDY_DETAIL,
        FEED,
        WORKSPACE,
        PROFILE,
        DAILY_QUIZ
    }

    public static class Message {
        private final String type; // "user", "ai", "system", "verdict"
        private final String sender;
        private final String content;

        public Message(String type, String content) {
            this(type, type, content);
        }

        public Message(String type, String sender, String content) {
            this.type = type;
            this.sender = sender;
            this.content = content;
        }

        public String getType() { return type; }
        public String getSender() { return sender; }
        public String getContent() { return content; }
    }

    private final Screen activeScreen;
    private final Case selectedCase;
    private final String selectedRole; // "savunma" or "savcilik"
    private final List<Message> chatMessages;
    private final int currentStepIndex;
    private final int accumulatedScore;
    private final List<Integer> stepScores; // scoreEffect earned per trial step, in order
    private final StudyTopic selectedStudyTopic;
    private final String leaderboardInterval; // "daily", "weekly", "monthly"
    
    // Auth parameters
    private final boolean isAuthenticated;
    private final String userDisplayName;
    
    // Theme & Localization
    private final boolean isDarkTheme;
    private final String language; // "tr" or "en"

    // Country Jurisdiction & Quiz Streaks
    private final String country; // "TR", "US", "UK", "DE"
    private final int dailyStreak;
    private final boolean dailyQuizCompleted;

    public AppState(Screen activeScreen, Case selectedCase, String selectedRole,
                    List<Message> chatMessages, int currentStepIndex, int accumulatedScore,
                    List<Integer> stepScores,
                    StudyTopic selectedStudyTopic, String leaderboardInterval,
                    boolean isAuthenticated, String userDisplayName, boolean isDarkTheme,
                    String language, String country, int dailyStreak, boolean dailyQuizCompleted) {
        this.activeScreen = activeScreen;
        this.selectedCase = selectedCase;
        this.selectedRole = selectedRole;
        this.chatMessages = chatMessages;
        this.currentStepIndex = currentStepIndex;
        this.accumulatedScore = accumulatedScore;
        this.stepScores = stepScores != null ? stepScores : new ArrayList<>();
        this.selectedStudyTopic = selectedStudyTopic;
        this.leaderboardInterval = leaderboardInterval;
        this.isAuthenticated = isAuthenticated;
        this.userDisplayName = userDisplayName;
        this.isDarkTheme = isDarkTheme;
        this.language = language != null ? language : "tr";
        this.country = country != null ? country : "TR";
        this.dailyStreak = dailyStreak >= 0 ? dailyStreak : 3;
        this.dailyQuizCompleted = dailyQuizCompleted;
    }

    public static AppState initial() {
        return new AppState(Screen.LOGIN, null, null, new ArrayList<>(), 0, 0, new ArrayList<>(), null, "daily", false, "Avukat", true, "tr", "TR", 3, false);
    }

    public Screen getActiveScreen() { return activeScreen; }
    public Case getSelectedCase() { return selectedCase; }
    public String getSelectedRole() { return selectedRole; }
    public List<Message> getChatMessages() { return chatMessages; }
    public int getCurrentStepIndex() { return currentStepIndex; }
    public int getAccumulatedScore() { return accumulatedScore; }
    public List<Integer> getStepScores() { return stepScores; }
    public StudyTopic getSelectedStudyTopic() { return selectedStudyTopic; }
    public String getLeaderboardInterval() { return leaderboardInterval; }
    public boolean isAuthenticated() { return isAuthenticated; }
    public String getUserDisplayName() { return userDisplayName; }

    public boolean isDarkTheme() { return isDarkTheme; }
    public String getLanguage() { return language; }

    public String getCountry() { return country; }
    public int getDailyStreak() { return dailyStreak; }
    public boolean isDailyQuizCompleted() { return dailyQuizCompleted; }

    public String getCareerRankTitle() {
        if (accumulatedScore >= 1000) {
            return "👑 Yargıtay Hakimi / Supreme Justice";
        } else if (accumulatedScore >= 500) {
            return "⚖️ Ağır Ceza Reisi / Senior Judge";
        } else if (accumulatedScore >= 200) {
            return "🥇 Uzman Avukat / Specialist Attorney";
        } else if (accumulatedScore >= 50) {
            return "🥈 Stajyer Avukat / Legal Intern";
        } else {
            return "🥉 Hukuk Öğrencisi / Law Student";
        }
    }

    public AppState copyWith(Screen activeScreen, Case selectedCase, String selectedRole,
                             List<Message> chatMessages, int currentStepIndex, int accumulatedScore,
                             List<Integer> stepScores,
                             StudyTopic selectedStudyTopic, String leaderboardInterval,
                             Boolean isAuthenticated, String userDisplayName, Boolean isDarkTheme,
                             String language, String country, Integer dailyStreak, Boolean dailyQuizCompleted) {
        return new AppState(
            activeScreen != null ? activeScreen : this.activeScreen,
            selectedCase != null ? selectedCase : this.selectedCase,
            selectedRole != null ? selectedRole : this.selectedRole,
            chatMessages != null ? chatMessages : this.chatMessages,
            currentStepIndex != -1 ? currentStepIndex : this.currentStepIndex,
            accumulatedScore != -1 ? accumulatedScore : this.accumulatedScore,
            stepScores != null ? stepScores : this.stepScores,
            selectedStudyTopic != null ? selectedStudyTopic : this.selectedStudyTopic,
            leaderboardInterval != null ? leaderboardInterval : this.leaderboardInterval,
            isAuthenticated != null ? isAuthenticated : this.isAuthenticated,
            userDisplayName != null ? userDisplayName : this.userDisplayName,
            isDarkTheme != null ? isDarkTheme : this.isDarkTheme,
            language != null ? language : this.language,
            country != null ? country : this.country,
            dailyStreak != null ? dailyStreak : this.dailyStreak,
            dailyQuizCompleted != null ? dailyQuizCompleted : this.dailyQuizCompleted
        );
    }
}
