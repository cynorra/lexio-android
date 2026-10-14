package com.cynorra.lexio.mvi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.cynorra.lexio.data.DateKeys;
import com.cynorra.lexio.data.UserProgress;
import com.cynorra.lexio.model.Case;
import com.cynorra.lexio.model.CasesData;

import org.junit.Before;
import org.junit.Test;

public class AppStoreTest {
    private AppStore store;
    private Case trCase;

    @Before
    public void setUp() {
        store = new AppStore();
        trCase = CasesData.getCasesForJurisdiction("TR").get(0);
    }

    private void startSimulation(String role) {
        store.dispatch(AppEvent.selectCase(trCase));
        store.dispatch(AppEvent.chooseRole(role));
        store.dispatch(AppEvent.of(AppEvent.Type.START_SIMULATION));
    }

    /** Plays every step, always picking the highest-scoring choice for the user's side. */
    private void playBest(boolean defense) {
        for (Case.TrialStep step : trCase.getTrialSteps()) {
            Case.Choice best = null;
            for (Case.Choice c : defense ? step.getDefenseChoices() : step.getProsecutionChoices()) {
                if (best == null || c.getScoreEffect() > best.getScoreEffect()) best = c;
            }
            store.dispatch(AppEvent.selectChoice(best));
        }
    }

    @Test
    public void chooseRoleIsStored() {
        startSimulation("savunma");
        assertEquals("savunma", store.getState().getSelectedRole());
    }

    @Test
    public void leaderboardIntervalIsStored() {
        store.dispatch(AppEvent.setLeaderboardInterval("monthly"));
        assertEquals("monthly", store.getState().getLeaderboardInterval());
    }

    @Test
    public void careerScoreSurvivesNewSimulation() {
        startSimulation("savunma");
        playBest(true);
        int earned = store.getState().getCareerScore();
        assertTrue(earned > 0);
        assertEquals(1, store.getState().getCasesSolved());

        store.dispatch(AppEvent.of(AppEvent.Type.NAVIGATE_TO_CASE_LIST));
        startSimulation("savunma");
        assertEquals(0, store.getState().getSimulationScore());
        assertEquals(earned, store.getState().getCareerScore());
    }

    @Test
    public void replayingACaseOnlyEarnsImprovement() {
        startSimulation("savunma");
        playBest(true);
        int first = store.getState().getCareerScore();

        startSimulation("savunma");
        playBest(true);
        assertEquals(first, store.getState().getCareerScore());
        assertEquals(1, store.getState().getCasesSolved());
    }

    @Test
    public void strayChoiceAfterVerdictIsIgnored() {
        startSimulation("savunma");
        playBest(true);
        int score = store.getState().getCareerScore();
        store.dispatch(AppEvent.selectChoice(trCase.getTrialSteps().get(0).getDefenseChoices().get(0)));
        assertEquals(score, store.getState().getCareerScore());
    }

    @Test
    public void prosecutorWinningGetsTheProsecutionVerdict() {
        startSimulation("savcilik");
        playBest(false);
        AppState.Message verdict = null;
        for (AppState.Message m : store.getState().getChatMessages()) {
            if (m.getSender().startsWith("⚖️")) verdict = m;
        }
        assertNotNull(verdict);
        assertEquals(trCase.getFinalVerdictDefenseLose(), verdict.getContent());
    }

    private void completeQuiz(int total, int correct, String today) {
        store.dispatch(AppEvent.of(AppEvent.Type.NAVIGATE_TO_DAILY_QUIZ));
        for (int i = 0; i < total; i++) {
            store.dispatch(AppEvent.quizAnswer(0, i < correct));
            store.dispatch(AppEvent.quizNext(total, today));
        }
    }

    @Test
    public void quizPaysProportionallyOncePerDay() {
        String today = DateKeys.today();
        store.dispatch(AppEvent.progressLoaded(UserProgress.empty(), today));

        completeQuiz(3, 2, today);
        assertEquals(67, store.getState().getCareerScore());
        assertEquals(1, store.getState().getDailyStreak());
        assertEquals(AppState.Screen.CASE_LIST, store.getState().getActiveScreen());

        completeQuiz(3, 3, today); // same day again: no reward, streak unchanged
        assertEquals(67, store.getState().getCareerScore());
        assertEquals(1, store.getState().getDailyStreak());
    }

    @Test
    public void quizStreakContinuesOnConsecutiveDaysAndResetsAfterAGap() {
        String today = DateKeys.today();
        String yesterday = DateKeys.previousDay(today);
        store.dispatch(AppEvent.progressLoaded(new UserProgress(10, 4, yesterday, null, null), today));
        assertEquals(4, store.getState().getDailyStreak()); // still alive

        completeQuiz(3, 3, today);
        assertEquals(5, store.getState().getDailyStreak());

        String longAgo = DateKeys.previousDay(DateKeys.previousDay(DateKeys.previousDay(today)));
        store.dispatch(AppEvent.progressLoaded(new UserProgress(10, 4, longAgo, null, null), today));
        assertEquals(0, store.getState().getDailyStreak()); // expired on load
    }

    @Test
    public void quizCannotAdvanceWithoutAnswering() {
        store.dispatch(AppEvent.of(AppEvent.Type.NAVIGATE_TO_DAILY_QUIZ));
        store.dispatch(AppEvent.quizNext(3, DateKeys.today()));
        assertEquals(0, store.getState().getQuizIndex());
    }

    @Test
    public void quizAnswerCountsOnlyOncePerQuestion() {
        store.dispatch(AppEvent.of(AppEvent.Type.NAVIGATE_TO_DAILY_QUIZ));
        store.dispatch(AppEvent.quizAnswer(1, true));
        store.dispatch(AppEvent.quizAnswer(2, true));
        assertEquals(1, store.getState().getQuizCorrect());
        assertEquals(1, store.getState().getQuizSelected());
    }

    @Test
    public void logoutClearsAccountButKeepsDevicePreferences() {
        store.dispatch(AppEvent.loginSuccess("Ada"));
        store.dispatch(AppEvent.progressLoaded(new UserProgress(50, 2, null, null, null), DateKeys.today()));
        store.dispatch(AppEvent.setLanguage("en"));
        store.dispatch(AppEvent.setDarkTheme(false));

        store.dispatch(AppEvent.of(AppEvent.Type.LOGOUT));

        AppState s = store.getState();
        assertEquals(AppState.Screen.LOGIN, s.getActiveScreen());
        assertFalse(s.isAuthenticated());
        assertFalse(s.isProgressLoaded());
        assertEquals(0, s.getCareerScore());
        assertEquals("en", s.getLanguage());
        assertFalse(s.isDarkTheme());
    }

    @Test
    public void progressMergeKeepsBestOfBothSides() {
        java.util.Map<Integer, Integer> a = new java.util.HashMap<>();
        a.put(1, 40);
        java.util.Map<Integer, Integer> b = new java.util.HashMap<>();
        b.put(1, 60);
        b.put(2, 30);
        UserProgress merged = UserProgress.merge(
                new UserProgress(100, 1, "2026-09-01", null, a),
                new UserProgress(80, 3, "2026-09-02", "ada", b));
        assertEquals(100, merged.careerScore);
        assertEquals(3, merged.dailyStreak);
        assertEquals("2026-09-02", merged.lastQuizDate);
        assertEquals("ada", merged.instagramHandle);
        assertEquals(Integer.valueOf(60), merged.caseBest.get(1));
        assertEquals(2, merged.caseBest.size());
    }

    @Test
    public void examSelectionIsStoredAndUnknownExamsAreIgnored() {
        assertEquals("us-ube", store.getState().getExam());
        store.dispatch(AppEvent.selectExam("tr-hmgs"));
        assertEquals("tr-hmgs", store.getState().getExam());
        store.dispatch(AppEvent.selectExam("not-an-exam"));
        assertEquals("tr-hmgs", store.getState().getExam());
    }

    @Test
    public void examQuizFinishesOnTheHubWithoutTouchingCareerPoints() {
        store.dispatch(AppEvent.of(AppEvent.Type.NAVIGATE_TO_EXAM_QUIZ));
        assertEquals(AppState.Screen.EXAM_QUIZ, store.getState().getActiveScreen());

        store.dispatch(AppEvent.examQuizNext(2)); // unanswered: ignored
        assertEquals(0, store.getState().getQuizIndex());

        store.dispatch(AppEvent.quizAnswer(1, true));
        store.dispatch(AppEvent.examQuizNext(2));
        assertEquals(1, store.getState().getQuizIndex());

        store.dispatch(AppEvent.quizAnswer(0, true));
        assertEquals(2, store.getState().getQuizCorrect());
        store.dispatch(AppEvent.examQuizNext(2));

        AppState s = store.getState();
        assertEquals(AppState.Screen.EXAM_PREP, s.getActiveScreen());
        assertEquals(0, s.getCareerScore());
        assertEquals(0, s.getDailyStreak());
        assertEquals(null, s.getLastQuizDate());
    }

    @Test
    public void logoutKeepsTheChosenExam() {
        store.dispatch(AppEvent.selectExam("uk-sqe1"));
        store.dispatch(AppEvent.of(AppEvent.Type.LOGOUT));
        assertEquals("uk-sqe1", store.getState().getExam());
    }
}
