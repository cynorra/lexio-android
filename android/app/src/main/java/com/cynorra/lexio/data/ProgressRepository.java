package com.cynorra.lexio.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Persists {@link UserProgress}: a per-user local cache (so the app works offline and a cold
 * start never shows zeros) plus the user's document in Firestore (source of truth across
 * devices).
 */
public class ProgressRepository {
    public interface Callback {
        /** @param remote the server copy, or null if the user has no document yet. */
        void onLoaded(UserProgress remote);

        /** The read failed (offline, rules, ...). Callers must not overwrite the server copy. */
        void onError();
    }

    private static final String PREFS = "lexio_progress";
    /** Global (not per-user) so the reminder worker can skip nagging after today's quiz. */
    public static final String KEY_LAST_QUIZ_DATE = "last_quiz_date";
    public static final String KEY_STREAK = "streak";

    private final SharedPreferences prefs;
    private final FirebaseFirestore db;

    public ProgressRepository(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        this.db = FirebaseFirestore.getInstance();
    }

    private String key(String uid, String field) {
        return uid + "." + field;
    }

    /** @return the locally cached progress, or null if this user was never seen on this device. */
    public UserProgress loadLocal(String uid) {
        if (!prefs.contains(key(uid, "score"))) return null;
        Map<Integer, Integer> best = new HashMap<>();
        try {
            JSONObject json = new JSONObject(prefs.getString(key(uid, "caseBest"), "{}"));
            Iterator<String> it = json.keys();
            while (it.hasNext()) {
                String id = it.next();
                best.put(Integer.parseInt(id), json.getInt(id));
            }
        } catch (JSONException | NumberFormatException ignored) {
            // Corrupt cache entry: fall back to whatever parsed; the server copy will merge in.
        }
        return new UserProgress(
                prefs.getInt(key(uid, "score"), 0),
                prefs.getInt(key(uid, "streak"), 0),
                prefs.getString(key(uid, "lastQuizDate"), null),
                prefs.getString(key(uid, "instagram"), null),
                best);
    }

    public void saveLocal(String uid, UserProgress p) {
        JSONObject best = new JSONObject();
        try {
            for (Map.Entry<Integer, Integer> e : p.caseBest.entrySet()) {
                best.put(String.valueOf(e.getKey()), e.getValue());
            }
        } catch (JSONException ignored) {
            // Integer keys/values can't make JSONObject throw.
        }
        prefs.edit()
                .putInt(key(uid, "score"), p.careerScore)
                .putInt(key(uid, "streak"), p.dailyStreak)
                .putString(key(uid, "lastQuizDate"), p.lastQuizDate)
                .putString(key(uid, "instagram"), p.instagramHandle)
                .putString(key(uid, "caseBest"), best.toString())
                .putString(KEY_LAST_QUIZ_DATE, p.lastQuizDate)
                .putInt(KEY_STREAK, p.dailyStreak)
                .apply();
    }

    /** Removes everything cached for this user on the device (used by account deletion). */
    public void clearLocal(String uid) {
        SharedPreferences.Editor editor = prefs.edit();
        for (String key : prefs.getAll().keySet()) {
            if (key.startsWith(uid + ".")) editor.remove(key);
        }
        editor.remove(KEY_LAST_QUIZ_DATE).remove(KEY_STREAK).apply();
    }

    public void loadRemote(String uid, Callback callback) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> callback.onLoaded(fromDocument(doc)))
                .addOnFailureListener(e -> callback.onError());
    }

    public void pushRemote(String uid, UserProgress p, String name, String email) {
        Map<String, Object> best = new HashMap<>();
        for (Map.Entry<Integer, Integer> e : p.caseBest.entrySet()) {
            best.put(String.valueOf(e.getKey()), e.getValue());
        }
        Map<String, Object> data = new HashMap<>();
        data.put("score", p.careerScore);
        data.put("dailyStreak", p.dailyStreak);
        data.put("lastQuizDate", p.lastQuizDate);
        data.put("instagramHandle", p.instagramHandle);
        data.put("caseBest", best);
        data.put("name", name);
        data.put("email", email);
        data.put("lastActive", System.currentTimeMillis());
        db.collection("users").document(uid).set(data, SetOptions.merge());
    }

    private static UserProgress fromDocument(DocumentSnapshot doc) {
        if (doc == null || !doc.exists()) return null;
        Long score = doc.getLong("score");
        Long streak = doc.getLong("dailyStreak");
        Map<Integer, Integer> best = new HashMap<>();
        Object raw = doc.get("caseBest");
        if (raw instanceof Map) {
            for (Map.Entry<?, ?> e : ((Map<?, ?>) raw).entrySet()) {
                try {
                    best.put(Integer.parseInt(String.valueOf(e.getKey())), ((Number) e.getValue()).intValue());
                } catch (NumberFormatException | ClassCastException ignored) {
                    // Skip malformed entries rather than dropping the whole document.
                }
            }
        }
        return new UserProgress(
                score != null ? score.intValue() : 0,
                streak != null ? streak.intValue() : 0,
                doc.getString("lastQuizDate"),
                doc.getString("instagramHandle"),
                best);
    }
}
