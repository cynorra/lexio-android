package com.lexio.app.mvi;

import com.lexio.app.model.Case;
import com.lexio.app.model.StudyTopic;

public class AppEvent {
    public enum Type {
        NAVIGATE_TO_LANDING,
        NAVIGATE_TO_CASE_LIST,
        SELECT_CASE,
        CHOOSE_ROLE,
        START_SIMULATION,
        SELECT_CHOICE,
        NAVIGATE_TO_STUDY_PORTAL,
        SELECT_STUDY_TOPIC,
        NAVIGATE_TO_LEADERBOARD,
        SET_LEADERBOARD_INTERVAL,
        RESET
    }

    private final Type type;
    private final Case selectedCase;
    private final String role;
    private final Case.Choice selectedChoice;
    private final StudyTopic selectedStudyTopic;
    private final String leaderboardInterval;

    public AppEvent(Type type) {
        this(type, null, null, null, null, null);
    }

    public AppEvent(Type type, Case selectedCase) {
        this(type, selectedCase, null, null, null, null);
    }

    public AppEvent(Type type, String data, boolean isRole) {
        this(type, null, isRole ? data : null, null, null, isRole ? null : data);
    }

    public AppEvent(Type type, Case.Choice selectedChoice) {
        this(type, null, null, selectedChoice, null, null);
    }

    public AppEvent(Type type, StudyTopic selectedStudyTopic) {
        this(type, null, null, null, selectedStudyTopic, null);
    }

    private AppEvent(Type type, Case selectedCase, String role, Case.Choice selectedChoice, 
                     StudyTopic selectedStudyTopic, String leaderboardInterval) {
        this.type = type;
        this.selectedCase = selectedCase;
        this.role = role;
        this.selectedChoice = selectedChoice;
        this.selectedStudyTopic = selectedStudyTopic;
        this.leaderboardInterval = leaderboardInterval;
    }

    public Type getType() { return type; }
    public Case getSelectedCase() { return selectedCase; }
    public String getRole() { return role; }
    public Case.Choice getSelectedChoice() { return selectedChoice; }
    public StudyTopic getSelectedStudyTopic() { return selectedStudyTopic; }
    public String getLeaderboardInterval() { return leaderboardInterval; }
}
