package com.cynorra.lexio.mvi;

import com.cynorra.lexio.data.DateKeys;
import com.cynorra.lexio.data.UserProgress;
import com.cynorra.lexio.model.Case;
import com.cynorra.lexio.model.Exams;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppStore {
    public interface Observer {
        void onStateChanged(AppState state);
    }

    /** Points a perfect daily quiz is worth; partial credit is proportional. */
    public static final int QUIZ_MAX_POINTS = 100;
    /** Share of the best possible simulation score needed for the user's side to win. */
    private static final float WIN_RATIO = 0.6f;

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
            for (Observer observer : new ArrayList<>(observers)) {
                observer.onStateChanged(state);
            }
        }
    }

    private AppState reduce(AppState current, AppEvent event) {
        switch (event.getType()) {
            case LOGIN_SUCCESS:
                return current.toBuilder()
                        .screen(AppState.Screen.LANDING)
                        .authenticated(true)
                        .userDisplayName(event.getText())
                        .build();

            case LOGOUT: {
                // Signing out clears the account, not the device's language/theme/jurisdiction.
                return AppState.initial().toBuilder()
                        .language(current.getLanguage())
                        .darkTheme(current.isDarkTheme())
                        .country(current.getCountry())
                        .exam(current.getExam())
                        .build();
            }

            case PROGRESS_LOADED: {
                AppState.Builder b = current.toBuilder();
                UserProgress p = event.getProgress();
                // A streak survives only if the last quiz was today or yesterday.
                String today = event.getToday();
                boolean alive = p.lastQuizDate != null
                        && (p.lastQuizDate.equals(today) || p.lastQuizDate.equals(DateKeys.previousDay(today)));
                b.progress(alive ? p : new UserProgress(
                        p.careerScore, 0, p.lastQuizDate, p.instagramHandle, p.caseBest));
                return b.build();
            }

            case NAVIGATE_TO_CASE_LIST:
                return current.toBuilder().screen(AppState.Screen.CASE_LIST).build();

            case SELECT_CASE:
                if (event.getSelectedCase() == null) return current;
                return current.toBuilder()
                        .screen(AppState.Screen.CASE_DETAIL)
                        .selectedCase(event.getSelectedCase())
                        .selectedRole(null)
                        .build();

            case CHOOSE_ROLE:
                return current.toBuilder().selectedRole(event.getText()).build();

            case START_SIMULATION:
                return startSimulation(current);

            case SELECT_CHOICE:
                return selectChoice(current, event.getSelectedChoice());

            case NAVIGATE_TO_STUDY_PORTAL:
                return current.toBuilder().screen(AppState.Screen.STUDY_PORTAL).build();

            case SELECT_STUDY_TOPIC:
                return current.toBuilder()
                        .screen(AppState.Screen.STUDY_DETAIL)
                        .selectedStudyTopic(event.getSelectedStudyTopic())
                        .build();

            case NAVIGATE_TO_LEADERBOARD:
                return current.toBuilder().screen(AppState.Screen.LEADERBOARD).build();

            case NAVIGATE_TO_FEED:
                return current.toBuilder().screen(AppState.Screen.FEED).build();

            case NAVIGATE_TO_WORKSPACE:
                return current.toBuilder().screen(AppState.Screen.WORKSPACE).build();

            case NAVIGATE_TO_PROFILE:
                return current.toBuilder().screen(AppState.Screen.PROFILE).build();

            case NAVIGATE_TO_DAILY_QUIZ:
                return current.toBuilder()
                        .screen(AppState.Screen.DAILY_QUIZ)
                        .quizIndex(0).quizCorrect(0).quizSelected(-1)
                        .build();

            case QUIZ_ANSWER: {
                if (current.getQuizSelected() != -1) return current; // already answered
                return current.toBuilder()
                        .quizSelected(event.getNumber())
                        .quizCorrect(current.getQuizCorrect() + (event.getFlag() ? 1 : 0))
                        .build();
            }

            case QUIZ_NEXT:
                return quizNext(current, event.getTotal(), event.getToday());

            case NAVIGATE_TO_EXAM_PREP:
                return current.toBuilder().screen(AppState.Screen.EXAM_PREP)
                        .quizIndex(0).quizCorrect(0).quizSelected(-1).build();

            case NAVIGATE_TO_EXAM_QUIZ:
                return current.toBuilder()
                        .screen(AppState.Screen.EXAM_QUIZ)
                        .quizIndex(0).quizCorrect(0).quizSelected(-1)
                        .build();

            case SELECT_EXAM:
                return Exams.isSupported(event.getText()) && !event.getText().equals(current.getExam())
                        ? current.toBuilder().exam(event.getText()).build() : current;

            case EXAM_QUIZ_NEXT:
                return examQuizNext(current, event.getTotal());

            case SELECT_COUNTRY:
                return current.toBuilder().country(event.getText()).build();

            case SET_INSTAGRAM_HANDLE:
                return current.toBuilder().instagramHandle(event.getText()).build();

            case TOGGLE_THEME:
                return current.toBuilder().darkTheme(!current.isDarkTheme()).build();

            case SET_DARK_THEME:
                return current.isDarkTheme() == event.getFlag() ? current
                        : current.toBuilder().darkTheme(event.getFlag()).build();

            case TOGGLE_LANGUAGE:
                return current.toBuilder()
                        .language("tr".equalsIgnoreCase(current.getLanguage()) ? "en" : "tr")
                        .build();

            case SET_LANGUAGE:
                return current.toBuilder().language(event.getText()).build();

            case SET_LEADERBOARD_INTERVAL:
                return current.toBuilder().leaderboardInterval(event.getText()).build();

            default:
                return current;
        }
    }

    private AppState startSimulation(AppState current) {
        Case selectedCase = current.getSelectedCase();
        if (selectedCase == null) return current;

        boolean isTrCase = "TR".equals(selectedCase.getJurisdiction());
        List<AppState.Message> intro = new ArrayList<>();

        intro.add(new AppState.Message("system", "<strong>" + selectedCase.getTitle() + "</strong> — " + selectedCase.getRef()));
        String summaryHeader = isTrCase ? "Vaka Özeti:" : "Case Summary:";
        intro.add(new AppState.Message("system", "<strong>" + summaryHeader + "</strong><br/>" + selectedCase.getFull()));

        String evidenceHeader = isTrCase ? "Dosyadaki Deliller:" : "Case Evidence:";
        StringBuilder evText = new StringBuilder("<strong>" + evidenceHeader + "</strong><br/>");
        for (String evidence : selectedCase.getEvidence()) {
            evText.append("• ").append(evidence).append("<br/>");
        }
        intro.add(new AppState.Message("system", evText.toString()));

        boolean userIsDefense = "savunma".equalsIgnoreCase(current.getSelectedRole());
        String opponentIntroRole = trialRoleLabel(!userIsDefense, isTrCase);
        String opening = userIsDefense
                ? (isTrCase
                    ? "Sayın Mahkeme, Savcılık sıfatıyla bu duruşmada müttehem aleyhine yeterli delil olduğunu ispatlayacağız. Deliller incelendiğinde eylemin yasal unsurları tam olarak taşıdığı görülecektir. Savunmanın argümanlarını bekliyoruz."
                    : "Your Honor, as the Prosecution, we will prove in this trial that there is sufficient evidence against the accused. Upon review of the evidence, it will be clear that the act meets every legal element of the offense. We await the defense's arguments.")
                : (isTrCase
                    ? "Sayın Mahkeme, müdafi olarak şunu belirtmeliyim: İddianame yüzeysel değerlendirmeye dayanmaktadır. Delillerin bütünlüklü değerlendirilmesi müvekkilimin iddia edilen suçu işlemediğini ortaya koyacaktır."
                    : "Your Honor, as defense counsel, I must state: the indictment rests on a superficial assessment. A full review of the evidence will show my client did not commit the alleged offense.");
        intro.add(new AppState.Message("ai", opponentIntroRole, opening));

        return current.toBuilder()
                .screen(AppState.Screen.SIMULATION)
                .chatMessages(intro)
                .currentStepIndex(0)
                .simulationScore(0)
                .stepScores(new ArrayList<>())
                .build();
    }

    private AppState selectChoice(AppState current, Case.Choice choice) {
        Case activeCase = current.getSelectedCase();
        if (choice == null || activeCase == null) return current;
        // The trial is over once every step was played; ignore stray taps.
        if (current.getCurrentStepIndex() >= activeCase.getTrialSteps().size()) return current;

        boolean isTrCase = "TR".equals(activeCase.getJurisdiction());
        boolean userIsDefense = "savunma".equalsIgnoreCase(current.getSelectedRole());

        List<AppState.Message> messages = new ArrayList<>(current.getChatMessages());
        messages.add(new AppState.Message("user", choice.getText()));
        messages.add(new AppState.Message("ai", trialRoleLabel(!userIsDefense, isTrCase), choice.getAiResponse()));

        int nextStepIndex = current.getCurrentStepIndex() + 1;
        int newScore = current.getSimulationScore() + choice.getScoreEffect();
        List<Integer> stepScores = new ArrayList<>(current.getStepScores());
        stepScores.add(choice.getScoreEffect());

        AppState.Builder b = current.toBuilder()
                .chatMessages(messages)
                .currentStepIndex(nextStepIndex)
                .simulationScore(newScore)
                .stepScores(stepScores);

        if (nextStepIndex >= activeCase.getTrialSteps().size()) {
            boolean userWon = newScore >= WIN_RATIO * maxPossibleScore(activeCase, userIsDefense);
            // The verdict texts are written from the defense's point of view.
            boolean defenseWon = userIsDefense == userWon;
            String verdict = defenseWon ? activeCase.getFinalVerdictDefenseWin() : activeCase.getFinalVerdictDefenseLose();
            String header = isTrCase ? "⚖️ Hakim Kararı" : "⚖️ Judge's Verdict";
            messages.add(new AppState.Message(header, verdict));

            // Only an improvement over the user's best on this case earns career points, so
            // replaying a case can't be farmed.
            Map<Integer, Integer> best = new HashMap<>(current.getCaseBest());
            Integer previous = best.get(activeCase.getId());
            int previousBest = previous != null ? previous : 0;
            int gain = Math.max(0, newScore - previousBest);
            best.put(activeCase.getId(), Math.max(previousBest, newScore));
            b.caseBest(best).careerScore(current.getCareerScore() + gain);
        }
        return b.build();
    }

    private static int maxPossibleScore(Case c, boolean forDefense) {
        int total = 0;
        for (Case.TrialStep step : c.getTrialSteps()) {
            int best = 0;
            for (Case.Choice ch : forDefense ? step.getDefenseChoices() : step.getProsecutionChoices()) {
                best = Math.max(best, ch.getScoreEffect());
            }
            total += best;
        }
        return Math.max(total, 1);
    }

    private AppState examQuizNext(AppState current, int total) {
        if (current.getQuizSelected() == -1 || total <= 0) return current; // must answer first
        if (current.getQuizIndex() + 1 < total) {
            return current.toBuilder().quizIndex(current.getQuizIndex() + 1).quizSelected(-1).build();
        }
        // Practice only: no points, no streak. The screen reads the score from the state it is leaving.
        return current.toBuilder().screen(AppState.Screen.EXAM_PREP)
                .quizIndex(0).quizCorrect(0).quizSelected(-1).build();
    }

    private AppState quizNext(AppState current, int total, String today) {
        if (current.getQuizSelected() == -1 || total <= 0) return current; // must answer first

        if (current.getQuizIndex() + 1 < total) {
            return current.toBuilder()
                    .quizIndex(current.getQuizIndex() + 1)
                    .quizSelected(-1)
                    .build();
        }

        AppState.Builder b = current.toBuilder()
                .screen(AppState.Screen.CASE_LIST)
                .quizIndex(0).quizCorrect(0).quizSelected(-1);

        // One reward per calendar day; finishing again later the same day earns nothing.
        if (!today.equals(current.getLastQuizDate())) {
            boolean continued = DateKeys.previousDay(today) != null
                    && DateKeys.previousDay(today).equals(current.getLastQuizDate());
            int bonus = Math.round(QUIZ_MAX_POINTS * current.getQuizCorrect() / (float) total);
            b.dailyStreak(continued ? current.getDailyStreak() + 1 : 1)
                    .lastQuizDate(today)
                    .careerScore(current.getCareerScore() + bonus);
        }
        return b.build();
    }

    /** Trial dialogue role labels follow the case's own jurisdiction language, not the
     *  app's UI language toggle, so a case's dialogue never mixes languages mid-trial. */
    private static String trialRoleLabel(boolean isProsecutor, boolean isTrCase) {
        if (isTrCase) {
            return isProsecutor ? "Savcı" : "Savunma Avukatı";
        }
        return isProsecutor ? "Prosecutor" : "Defense Counsel";
    }
}
