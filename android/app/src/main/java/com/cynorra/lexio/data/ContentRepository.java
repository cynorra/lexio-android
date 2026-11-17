package com.cynorra.lexio.data;

import com.cynorra.lexio.model.Case;
import com.cynorra.lexio.model.DailyQuizQuestion;
import com.cynorra.lexio.model.StudyTopic;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Reads the daily quizzes and study sets that tools/quizgen publishes to Firestore
 * ({@code dailyQuizzes/{CC}/days/{date}} and {@code studySets/{CC}/sets/{slug}}). All content
 * is bilingual ("en"/"tr"); the language is picked here so callers get plain strings.
 */
public class ContentRepository {
    public interface QuizCallback {
        void onLoaded(List<DailyQuizQuestion> questions);
        void onError();

        /** Called just before onLoaded with the date of the quiz that was found (may be older than today). */
        default void onQuizDate(String date) { }
    }

    public interface ExistsCallback {
        void onResult(boolean exists);
    }

    /** Whether the generator has published this country's quiz for {@code date} (false = only older ones exist). */
    public void quizExists(String country, String date, ExistsCallback callback) {
        db.collection("dailyQuizzes").document(country).collection("days").document(date).get()
                .addOnSuccessListener(doc -> callback.onResult(doc.exists()))
                .addOnFailureListener(e -> callback.onResult(true)); // offline: don't claim the test is missing
    }

    public interface StudyCallback {
        void onLoaded(List<StudyTopic> topics);
        void onError();
    }

    private static final int STUDY_LIMIT = 30;
    private static final int CASE_LIMIT = 60;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Loads today's quiz for the country; if the generator hasn't published that day yet (or the
     * user's local date is ahead of it), falls back to the most recent published quiz so the
     * screen is never empty. Firestore's offline cache serves both reads without a connection.
     */
    public void loadDailyQuiz(String country, String date, String language, QuizCallback callback) {
        loadQuiz(db.collection("dailyQuizzes").document(country).collection("days"), country, date, language, callback);
    }

    /** Same, for a law exam's own daily quiz ({@code examQuizzes/{examId}/days/{date}}). */
    public void loadExamQuiz(String examId, String date, String language, QuizCallback callback) {
        loadQuiz(db.collection("examQuizzes").document(examId).collection("days"), examId, date, language, callback);
    }

    private void loadQuiz(CollectionReference days, String country, String date, String language, QuizCallback callback) {
        days.document(date).get()
                .addOnSuccessListener(doc -> {
                    List<DailyQuizQuestion> questions = doc.exists()
                            ? parseQuestions(doc.get("questions"), language, country) : null;
                    if (questions != null && !questions.isEmpty()) {
                        callback.onQuizDate(date);
                        callback.onLoaded(questions);
                    } else {
                        loadLatestQuiz(days, country, language, callback);
                    }
                })
                .addOnFailureListener(e -> callback.onError());
    }

    private static final int PRACTICE_DAYS = 21;
    private static final int PRACTICE_SIZE = 10;

    /**
     * Unlimited extra practice: a random mix of questions from the country's recent daily quizzes
     * ({@code dailyQuizzes/{CC}/days}, newest {@value #PRACTICE_DAYS} days). Nothing is rewarded.
     */
    public void loadPracticeQuestions(String country, String language, QuizCallback callback) {
        db.collection("dailyQuizzes").document(country).collection("days")
                .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
                .limit(PRACTICE_DAYS)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<DailyQuizQuestion> pool = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        pool.addAll(parseQuestions(doc.get("questions"), language, country));
                    }
                    if (pool.isEmpty()) {
                        callback.onError();
                        return;
                    }
                    // Older days can contain a near-identical question on the same topic: show each only once.
                    java.util.Collections.shuffle(pool);
                    List<DailyQuizQuestion> unique = new ArrayList<>();
                    java.util.Set<String> seen = new java.util.HashSet<>();
                    for (DailyQuizQuestion q : pool) {
                        String key = q.getQuestion().toLowerCase(java.util.Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]+", " ").trim();
                        if (key.length() > 60) key = key.substring(0, 60);
                        if (seen.add(key)) unique.add(q);
                        if (unique.size() == PRACTICE_SIZE) break;
                    }
                    callback.onLoaded(unique);
                })
                .addOnFailureListener(e -> callback.onError());
    }

    private void loadLatestQuiz(CollectionReference days, String country, String language, QuizCallback callback) {
        days.orderBy(FieldPath.documentId(), Query.Direction.DESCENDING).limit(1).get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        List<DailyQuizQuestion> questions = parseQuestions(doc.get("questions"), language, country);
                        if (!questions.isEmpty()) {
                            callback.onQuizDate(doc.getId());
                            callback.onLoaded(questions);
                            return;
                        }
                    }
                    callback.onError();
                })
                .addOnFailureListener(e -> callback.onError());
    }

    public interface CaseCallback {
        void onLoaded(List<Case> cases);
        void onError();
    }

    /** Generated trial-simulation cases ({@code caseLibrary/{CC}/cases/{slug}}), newest first. English only. */
    public void loadCases(String country, CaseCallback callback) {
        db.collection("caseLibrary").document(country).collection("cases")
                .orderBy("publishedAt", Query.Direction.DESCENDING)
                .limit(CASE_LIMIT)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Case> cases = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Case parsed = parseCase(doc.getData(), country);
                        if (parsed != null) cases.add(parsed);
                    }
                    callback.onLoaded(cases);
                })
                .addOnFailureListener(e -> callback.onError());
    }

    /** A malformed case is skipped (null) rather than failing the whole list or crashing the simulation. */
    static Case parseCase(Map<String, Object> d, String country) {
        if (d == null) return null;
        try {
            Object id = d.get("id");
            String title = str(d.get("title"));
            String full = str(d.get("full"));
            if (!(id instanceof Number) || title.isEmpty() || full.isEmpty()) return null;

            List<String> evidence = new ArrayList<>();
            if (d.get("evidence") instanceof List) {
                for (Object e : (List<?>) d.get("evidence")) {
                    if (e instanceof String && !((String) e).isEmpty()) evidence.add((String) e);
                }
            }
            List<Case.TrialStep> steps = new ArrayList<>();
            if (d.get("trialSteps") instanceof List) {
                for (Object o : (List<?>) d.get("trialSteps")) {
                    if (!(o instanceof Map)) return null;
                    Map<?, ?> st = (Map<?, ?>) o;
                    List<Case.Choice> defense = parseChoices(st.get("defenseChoices"));
                    List<Case.Choice> prosecution = parseChoices(st.get("prosecutionChoices"));
                    if (defense.isEmpty() || prosecution.isEmpty()) return null;
                    steps.add(new Case.TrialStep(str(st.get("stepName")), str(st.get("prompt")), defense, prosecution));
                }
            }
            if (steps.isEmpty()) return null;

            return new Case(
                    ((Number) id).intValue(),
                    str(d.get("tag")),
                    str(d.get("tagLabel")),
                    title,
                    str(d.get("preview")),
                    str(d.get("difficulty")),
                    str(d.get("duration")),
                    str(d.get("ref")),
                    full,
                    evidence,
                    steps,
                    str(d.get("finalVerdictDefenseWin")),
                    str(d.get("finalVerdictDefenseLose")),
                    country,
                    true);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static List<Case.Choice> parseChoices(Object raw) {
        List<Case.Choice> result = new ArrayList<>();
        if (!(raw instanceof List)) return result;
        for (Object o : (List<?>) raw) {
            if (!(o instanceof Map)) continue;
            Map<?, ?> c = (Map<?, ?>) o;
            String text = str(c.get("text"));
            if (text.isEmpty() || !(c.get("scoreEffect") instanceof Number)) continue;
            result.add(new Case.Choice(text, str(c.get("aiResponse")), ((Number) c.get("scoreEffect")).intValue()));
        }
        return result;
    }

    private static String str(Object o) {
        return o instanceof String ? (String) o : "";
    }

    public void loadStudySets(String country, String language, StudyCallback callback) {
        db.collection("studySets").document(country).collection("sets")
                .orderBy("publishedAt", Query.Direction.DESCENDING)
                .limit(STUDY_LIMIT)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<StudyTopic> topics = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        StudyTopic topic = parseStudyTopic(doc, language, country);
                        if (topic != null) topics.add(topic);
                    }
                    callback.onLoaded(topics);
                })
                .addOnFailureListener(e -> callback.onError());
    }

    private static StudyTopic parseStudyTopic(DocumentSnapshot doc, String language, String country) {
        String title = localized(doc.get("title"), language);
        String lesson = localized(doc.get("lesson"), language);
        if (title.isEmpty() || lesson.isEmpty()) return null;
        Long minutes = doc.getLong("minutes");
        String reference = doc.getString("reference");
        return new StudyTopic(
                doc.getId(),
                title,
                localized(doc.get("category"), language),
                localized(doc.get("summary"), language),
                lesson,
                minutes != null ? minutes.intValue() : 5,
                reference != null ? reference : "",
                parseQuestions(doc.get("questions"), language, country));
    }

    /** Picks {@code language} from a {"en": ..., "tr": ...} map, falling back to English. */
    private static String localized(Object bilingual, String language) {
        if (!(bilingual instanceof Map)) return "";
        Map<?, ?> map = (Map<?, ?>) bilingual;
        Object value = map.get(language);
        if (!(value instanceof String) || ((String) value).isEmpty()) value = map.get("en");
        return value instanceof String ? (String) value : "";
    }

    /** Malformed questions are skipped rather than failing the whole quiz. */
    private static List<DailyQuizQuestion> parseQuestions(Object raw, String language, String country) {
        List<DailyQuizQuestion> result = new ArrayList<>();
        if (!(raw instanceof List)) return result;
        for (Object item : (List<?>) raw) {
            if (!(item instanceof Map)) continue;
            Map<?, ?> q = (Map<?, ?>) item;
            Object loc = q.get(language) instanceof Map ? q.get(language) : q.get("en");
            if (!(loc instanceof Map) || !(q.get("correct_index") instanceof Number)) continue;
            Map<?, ?> l = (Map<?, ?>) loc;

            List<String> options = new ArrayList<>();
            if (l.get("options") instanceof List) {
                for (Object o : (List<?>) l.get("options")) {
                    if (o instanceof String) options.add((String) o);
                }
            }
            int correct = ((Number) q.get("correct_index")).intValue();
            Object question = l.get("question");
            Object explanation = l.get("explanation");
            if (!(question instanceof String) || options.size() < 2 || correct < 0 || correct >= options.size()) continue;

            result.add(new DailyQuizQuestion(
                    String.valueOf(q.get("id")),
                    (String) question,
                    options,
                    correct,
                    explanation instanceof String ? (String) explanation : "",
                    country));
        }
        return result;
    }
}
