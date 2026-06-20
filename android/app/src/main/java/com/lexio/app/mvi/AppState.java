package com.lexio.app.mvi;

import com.lexio.app.model.Case;
import com.lexio.app.model.StudyTopic;
import java.util.ArrayList;
import java.util.List;

public class AppState {
    public enum Screen {
        LANDING,
        CASE_LIST,
        CASE_DETAIL,
        SIMULATION,
        LEADERBOARD,
        STUDY_PORTAL,
        STUDY_DETAIL
    }

    public static class Message {
        private final String sender; // "user", "ai_counsel", "ai_judge", "system"
        private final String content;

        public Message(String sender, String content) {
            this.sender = sender;
            this.content = content;
        }

        public String getSender() { return sender; }
        public String getContent() { return content; }
    }

    private final Screen activeScreen;
    private final Case selectedCase;
    private final String selectedRole; // "savunma" or "savcilik"
    private final List<Message> chatMessages;
    private final int currentStepIndex;
    private final int accumulatedScore;
    
    // Online Leaderboard & Study portal parameters
    private final StudyTopic selectedStudyTopic;
    private final String leaderboardInterval; // "daily", "weekly", "monthly"

    public AppState(Screen activeScreen, Case selectedCase, String selectedRole, 
                    List<Message> chatMessages, int currentStepIndex, int accumulatedScore,
                    StudyTopic selectedStudyTopic, String leaderboardInterval) {
        this.activeScreen = activeScreen;
        this.selectedCase = selectedCase;
        this.selectedRole = selectedRole;
        this.chatMessages = chatMessages;
        this.currentStepIndex = currentStepIndex;
        this.accumulatedScore = accumulatedScore;
        this.selectedStudyTopic = selectedStudyTopic;
        this.leaderboardInterval = leaderboardInterval;
    }

    public static AppState initial() {
        return new AppState(Screen.LANDING, null, null, new ArrayList<>(), 0, 0, null, "daily");
    }

    public Screen getActiveScreen() { return activeScreen; }
    public Case getSelectedCase() { return selectedCase; }
    public String getSelectedRole() { return selectedRole; }
    public List<Message> getChatMessages() { return chatMessages; }
    public int getCurrentStepIndex() { return currentStepIndex; }
    public int getAccumulatedScore() { return accumulatedScore; }
    public StudyTopic getSelectedStudyTopic() { return selectedStudyTopic; }
    public String getLeaderboardInterval() { return leaderboardInterval; }

    public AppState copyWith(Screen activeScreen, Case selectedCase, String selectedRole, 
                             List<Message> chatMessages, int currentStepIndex, int accumulatedScore,
                             StudyTopic selectedStudyTopic, String leaderboardInterval) {
        return new AppState(
            activeScreen != null ? activeScreen : this.activeScreen,
            selectedCase != null ? selectedCase : this.selectedCase,
            selectedRole != null ? selectedRole : this.selectedRole,
            chatMessages != null ? chatMessages : this.chatMessages,
            currentStepIndex != -1 ? currentStepIndex : this.currentStepIndex,
            accumulatedScore != -1 ? accumulatedScore : this.accumulatedScore,
            selectedStudyTopic != null ? selectedStudyTopic : this.selectedStudyTopic,
            leaderboardInterval != null ? leaderboardInterval : this.leaderboardInterval
        );
    }
}
