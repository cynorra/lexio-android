package com.cynorra.lexio.data;

import com.cynorra.lexio.model.LeaderboardEntry;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Period leaderboards live in {@code leaderboards/{periodKey}/entries/{uid}}, one document
 * per user per period holding the points earned in that period. Keying by period means the
 * daily/weekly/monthly tabs show real, separate rankings and need no composite index.
 */
public class LeaderboardRepository {
    public interface Callback {
        void onLoaded(List<LeaderboardEntry> entries);
        void onError();
    }

    private static final int LIMIT = 20;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /** Adds {@code points} earned just now to today's, this week's and this month's boards. */
    public void addPoints(String uid, String name, int points) {
        if (points <= 0) return;
        WriteBatch batch = db.batch();
        String[] periodKeys = {DateKeys.dailyKey(), DateKeys.weeklyKey(), DateKeys.monthlyKey()};
        for (String periodKey : periodKeys) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("score", FieldValue.increment(points));
            data.put("updatedAt", System.currentTimeMillis());
            batch.set(db.collection("leaderboards").document(periodKey).collection("entries").document(uid),
                    data, SetOptions.merge());
        }
        // Remember which boards this user is on, so account deletion can remove every entry
        // (the period documents can't be listed from the client).
        Map<String, Object> boards = new HashMap<>();
        boards.put("boards", FieldValue.arrayUnion((Object[]) periodKeys));
        batch.set(db.collection("users").document(uid), boards, SetOptions.merge());
        batch.commit();
    }

    public void load(String interval, String currentUid, String defaultName, String youSuffix, Callback callback) {
        db.collection("leaderboards").document(DateKeys.keyForInterval(interval)).collection("entries")
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(LIMIT)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<LeaderboardEntry> list = new ArrayList<>();
                    int rank = 1;
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Long score = doc.getLong("score");
                        if (score == null || score <= 0) continue;
                        String name = doc.getString("name");
                        if (name == null || name.isEmpty()) name = defaultName;
                        boolean isCurrent = doc.getId().equals(currentUid);
                        list.add(new LeaderboardEntry(rank++, isCurrent ? name + youSuffix : name,
                                score.intValue(), isCurrent));
                    }
                    callback.onLoaded(list);
                })
                .addOnFailureListener(e -> callback.onError());
    }
}
