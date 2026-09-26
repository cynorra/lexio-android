package com.cynorra.lexio.mvi;

import com.cynorra.lexio.data.UserProgress;
import com.cynorra.lexio.model.Case;
import com.cynorra.lexio.model.Exams;
import com.cynorra.lexio.model.Jurisdictions;
import com.cynorra.lexio.model.StudyTopic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        DAILY_QUIZ,
        EXAM_PREP,
        EXAM_QUIZ
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

    // Navigation / simulation
    private final Screen activeScreen;
    private final Case selectedCase;
    private final String selectedRole; // "savunma" or "savcilik"
    private final List<Message> chatMessages;
    private final int currentStepIndex;
    private final int simulationScore; // score of the simulation in progress (not the career total)
    private final List<Integer> stepScores; // scoreEffect earned per trial step, in order
    private final StudyTopic selectedStudyTopic;
    private final String leaderboardInterval; // "daily", "weekly", "monthly"

    // Auth
    private final boolean isAuthenticated;
    private final String userDisplayName; // "" until known; the UI substitutes a localized default

    // Theme & localization
    private final boolean isDarkTheme;
    private final String language; // "tr" or "en"
    private final String country; // "TR", "US", "UK", "DE"
    private final String exam; // id of the law exam the user is preparing for (see Exams)

    // Persistent progress (see UserProgress)
    private final boolean progressLoaded;
    private final int progressEpoch; // bumped whenever progress is replaced wholesale (login/sync)
    private final int careerScore;
    private final int dailyStreak;
    private final String lastQuizDate;
    private final String instagramHandle;
    private final Map<Integer, Integer> caseBest;

    // Daily quiz in progress
    private final int quizIndex;
    private final int quizCorrect;
    private final int quizSelected; // option picked for the current question, -1 if unanswered

    private AppState(Builder b) {
        this.activeScreen = b.activeScreen;
        this.selectedCase = b.selectedCase;
        this.selectedRole = b.selectedRole;
        this.chatMessages = b.chatMessages;
        this.currentStepIndex = b.currentStepIndex;
        this.simulationScore = b.simulationScore;
        this.stepScores = b.stepScores;
        this.selectedStudyTopic = b.selectedStudyTopic;
        this.leaderboardInterval = b.leaderboardInterval;
        this.isAuthenticated = b.isAuthenticated;
        this.userDisplayName = b.userDisplayName;
        this.isDarkTheme = b.isDarkTheme;
        this.language = b.language;
        this.country = b.country;
        this.exam = b.exam;
        this.progressLoaded = b.progressLoaded;
        this.progressEpoch = b.progressEpoch;
        this.careerScore = b.careerScore;
        this.dailyStreak = b.dailyStreak;
        this.lastQuizDate = b.lastQuizDate;
        this.instagramHandle = b.instagramHandle;
        this.caseBest = Collections.unmodifiableMap(new HashMap<>(b.caseBest));
        this.quizIndex = b.quizIndex;
        this.quizCorrect = b.quizCorrect;
        this.quizSelected = b.quizSelected;
    }

    public static AppState initial() {
        return new Builder().build();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public Screen getActiveScreen() { return activeScreen; }
    public Case getSelectedCase() { return selectedCase; }
    public String getSelectedRole() { return selectedRole; }
    public List<Message> getChatMessages() { return chatMessages; }
    public int getCurrentStepIndex() { return currentStepIndex; }
    public int getSimulationScore() { return simulationScore; }
    public List<Integer> getStepScores() { return stepScores; }
    public StudyTopic getSelectedStudyTopic() { return selectedStudyTopic; }
    public String getLeaderboardInterval() { return leaderboardInterval; }
    public boolean isAuthenticated() { return isAuthenticated; }
    public String getUserDisplayName() { return userDisplayName; }
    public boolean isDarkTheme() { return isDarkTheme; }
    public String getLanguage() { return language; }
    public String getCountry() { return country; }
    public String getExam() { return exam; }

    public boolean isProgressLoaded() { return progressLoaded; }
    public int getProgressEpoch() { return progressEpoch; }
    public int getCareerScore() { return careerScore; }
    public int getDailyStreak() { return dailyStreak; }
    public String getLastQuizDate() { return lastQuizDate; }
    public String getInstagramHandle() { return instagramHandle; }
    public Map<Integer, Integer> getCaseBest() { return caseBest; }
    public int getCasesSolved() { return caseBest.size(); }

    public int getQuizIndex() { return quizIndex; }
    public int getQuizCorrect() { return quizCorrect; }
    public int getQuizSelected() { return quizSelected; }

    public UserProgress toProgress() {
        return new UserProgress(careerScore, dailyStreak, lastQuizDate, instagramHandle, caseBest);
    }

    public static class Builder {
        Screen activeScreen = Screen.LOGIN;
        Case selectedCase = null;
        String selectedRole = null;
        List<Message> chatMessages = new ArrayList<>();
        int currentStepIndex = 0;
        int simulationScore = 0;
        List<Integer> stepScores = new ArrayList<>();
        StudyTopic selectedStudyTopic = null;
        String leaderboardInterval = "daily";
        boolean isAuthenticated = false;
        String userDisplayName = "";
        boolean isDarkTheme = true;
        String language = "tr";
        String country = Jurisdictions.DEFAULT;
        String exam = Exams.DEFAULT;
        boolean progressLoaded = false;
        int progressEpoch = 0;
        int careerScore = 0;
        int dailyStreak = 0;
        String lastQuizDate = null;
        String instagramHandle = null;
        Map<Integer, Integer> caseBest = new HashMap<>();
        int quizIndex = 0;
        int quizCorrect = 0;
        int quizSelected = -1;

        Builder() {}

        Builder(AppState s) {
            activeScreen = s.activeScreen;
            selectedCase = s.selectedCase;
            selectedRole = s.selectedRole;
            chatMessages = s.chatMessages;
            currentStepIndex = s.currentStepIndex;
            simulationScore = s.simulationScore;
            stepScores = s.stepScores;
            selectedStudyTopic = s.selectedStudyTopic;
            leaderboardInterval = s.leaderboardInterval;
            isAuthenticated = s.isAuthenticated;
            userDisplayName = s.userDisplayName;
            isDarkTheme = s.isDarkTheme;
            language = s.language;
            country = s.country;
            exam = s.exam;
            progressLoaded = s.progressLoaded;
            progressEpoch = s.progressEpoch;
            careerScore = s.careerScore;
            dailyStreak = s.dailyStreak;
            lastQuizDate = s.lastQuizDate;
            instagramHandle = s.instagramHandle;
            caseBest = new HashMap<>(s.caseBest);
            quizIndex = s.quizIndex;
            quizCorrect = s.quizCorrect;
            quizSelected = s.quizSelected;
        }

        public Builder screen(Screen v) { activeScreen = v; return this; }
        public Builder selectedCase(Case v) { selectedCase = v; return this; }
        public Builder selectedRole(String v) { selectedRole = v; return this; }
        public Builder chatMessages(List<Message> v) { chatMessages = v; return this; }
        public Builder currentStepIndex(int v) { currentStepIndex = v; return this; }
        public Builder simulationScore(int v) { simulationScore = v; return this; }
        public Builder stepScores(List<Integer> v) { stepScores = v; return this; }
        public Builder selectedStudyTopic(StudyTopic v) { selectedStudyTopic = v; return this; }
        public Builder leaderboardInterval(String v) { leaderboardInterval = v; return this; }
        public Builder authenticated(boolean v) { isAuthenticated = v; return this; }
        public Builder userDisplayName(String v) { userDisplayName = v != null ? v : ""; return this; }
        public Builder darkTheme(boolean v) { isDarkTheme = v; return this; }
        public Builder language(String v) { language = v != null ? v : "tr"; return this; }
        public Builder country(String v) { country = v != null ? v : Jurisdictions.DEFAULT; return this; }
        public Builder exam(String v) { exam = v != null ? v : Exams.DEFAULT; return this; }
        public Builder careerScore(int v) { careerScore = v; return this; }
        public Builder dailyStreak(int v) { dailyStreak = v; return this; }
        public Builder lastQuizDate(String v) { lastQuizDate = v; return this; }
        public Builder instagramHandle(String v) { instagramHandle = v; return this; }
        public Builder caseBest(Map<Integer, Integer> v) { caseBest = new HashMap<>(v); return this; }
        public Builder quizIndex(int v) { quizIndex = v; return this; }
        public Builder quizCorrect(int v) { quizCorrect = v; return this; }
        public Builder quizSelected(int v) { quizSelected = v; return this; }

        /** Replaces all persistent progress and marks it as (re)loaded. */
        public Builder progress(UserProgress p) {
            careerScore = p.careerScore;
            dailyStreak = p.dailyStreak;
            lastQuizDate = p.lastQuizDate;
            instagramHandle = p.instagramHandle;
            caseBest = new HashMap<>(p.caseBest);
            progressLoaded = true;
            progressEpoch++;
            return this;
        }

        public AppState build() { return new AppState(this); }
    }
}
