package com.lexio.app.mvi;

import com.lexio.app.model.Case;
import java.util.ArrayList;
import java.util.List;

public class AppStore {
    public interface Observer {
        void onStateChanged(AppState state);
    }

    private AppState state;
    private final List<Observer> observers = new ArrayList<>();

    public AppStore() {
        this.state = AppState.initial();
    }

    public synchronized AppState getState() {
        return state;
    }

    public synchronized void subscribe(Observer observer) {
        observers.add(observer);
        observer.onStateChanged(state);
    }

    public synchronized void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    public synchronized void dispatch(AppEvent event) {
        AppState newState = reduce(state, event);
        if (newState != state) {
            state = newState;
            for (Observer observer : observers) {
                observer.onStateChanged(state);
            }
        }
    }

    private AppState reduce(AppState current, AppEvent event) {
        switch (event.getType()) {
            case LOGIN_SUCCESS:
                return current.copyWith(
                    AppState.Screen.LANDING, 
                    null, null, null, -1, -1, null, null, 
                    true, 
                    event.getUserDisplayName()
                );
            case LOGOUT:
                return AppState.initial();
            case NAVIGATE_TO_LANDING:
                return current.copyWith(AppState.Screen.LANDING, null, null, new ArrayList<>(), 0, 0, null, "daily", null, null);
            case NAVIGATE_TO_CASE_LIST:
                return current.copyWith(AppState.Screen.CASE_LIST, null, null, null, -1, -1, null, null, null, null);
            case SELECT_CASE:
                return current.copyWith(AppState.Screen.CASE_DETAIL, event.getSelectedCase(), null, null, -1, -1, null, null, null, null);
            case CHOOSE_ROLE:
                return current.copyWith(null, null, event.getRole(), null, -1, -1, null, null, null, null);
            case START_SIMULATION:
                List<AppState.Message> introMessages = new ArrayList<>();
                Case selectedCase = current.getSelectedCase();
                
                introMessages.add(new AppState.Message("system", "<strong>" + selectedCase.getTitle() + "</strong> — " + selectedCase.getRef()));
                introMessages.add(new AppState.Message("system", "<strong>Vaka Özeti:</strong><br/>" + selectedCase.getFull()));
                
                StringBuilder evText = new StringBuilder("<strong>Dosyadaki Deliller:</strong><br/>");
                for (String evidence : selectedCase.getEvidence()) {
                    evText.append("• ").append(evidence).append("<br/>");
                }
                introMessages.add(new AppState.Message("system", evText.toString()));

                String aiRole = "savunma".equalsIgnoreCase(current.getSelectedRole()) ? "Savcı" : "Savunma Avukatı";
                String opening = "savunma".equalsIgnoreCase(current.getSelectedRole())
                    ? "Sayın Mahkeme, Savcılık sıfatıyla bu duruşmada müttehem aleyhine yeterli delil olduğunu ispatlayacağız. Deliller incelendiğinde eylemin yasal unsurları tam olarak taşıdığı görülecektir. Savunmanın argümanlarını bekliyoruz."
                    : "Sayın Mahkeme, müdafi olarak şunu belirtmeliyim: İddianame yüzeysel değerlendirmeye dayanmaktadır. Delillerin bütünlüklü değerlendirilmesi müvekkilimin iddia edilen suçu işlemediğini ortaya koyacaktır.";
                
                introMessages.add(new AppState.Message("ai", "AI " + aiRole, opening));
                
                return current.copyWith(AppState.Screen.SIMULATION, null, null, introMessages, 0, 0, null, null, null, null);

            case SELECT_CHOICE:
                Case.Choice choice = event.getSelectedChoice();
                if (choice == null) return current;

                List<AppState.Message> updatedMessages = new ArrayList<>(current.getChatMessages());
                
                // 1. Add User Choice
                String userLabel = "savunma".equalsIgnoreCase(current.getSelectedRole()) ? "Savunma Avukatı" : "Savcı";
                updatedMessages.add(new AppState.Message("user", choice.getText()));

                // 2. Add AI Response corresponding to that choice
                String opponentRole = "savunma".equalsIgnoreCase(current.getSelectedRole()) ? "AI Savcı" : "AI Savunma Avukatı";
                updatedMessages.add(new AppState.Message("ai", opponentRole, choice.getAiResponse()));

                int nextStepIndex = current.getCurrentStepIndex() + 1;
                int newScore = current.getAccumulatedScore() + choice.getScoreEffect();

                Case activeCase = current.getSelectedCase();
                if (activeCase != null && nextStepIndex >= activeCase.getTrialSteps().size()) {
                    // Simulation complete!
                    boolean defenseWin = newScore >= 60;
                    String finalVerdict = defenseWin ? activeCase.getFinalVerdictDefenseWin() : activeCase.getFinalVerdictDefenseLose();
                    updatedMessages.add(new AppState.Message("⚖️ Hakim — AI Kararı", finalVerdict));
                }

                return current.copyWith(null, null, null, updatedMessages, nextStepIndex, newScore, null, null, null, null);

            case NAVIGATE_TO_STUDY_PORTAL:
                return current.copyWith(AppState.Screen.STUDY_PORTAL, null, null, null, -1, -1, null, null, null, null);

            case SELECT_STUDY_TOPIC:
                return current.copyWith(AppState.Screen.STUDY_DETAIL, null, null, null, -1, -1, event.getSelectedStudyTopic(), null, null, null);

            case NAVIGATE_TO_LEADERBOARD:
                return current.copyWith(AppState.Screen.LEADERBOARD, null, null, null, -1, -1, null, null, null, null);

            case SET_LEADERBOARD_INTERVAL:
                return current.copyWith(null, null, null, null, -1, -1, null, event.getLeaderboardInterval(), null, null);

            case RESET:
                return AppState.initial();
            default:
                return current;
        }
    }
}
