package com.cynorra.lexio.data;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

/**
 * Deletes everything the app stored about a user in Firestore, as Google Play's account-deletion
 * policy requires: the profile/progress document, the user's leaderboard entries and their feed
 * posts. (The Firebase Auth account itself is deleted by the caller afterwards.)
 */
public class AccountRepository {
    public interface Callback {
        void onDone();
        void onError(Exception e);
    }

    /** Firestore allows 500 writes per batch; stay below it. */
    private static final int BATCH_SIZE = 400;
    private static final int MAX_POSTS = 1000;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void deleteUserData(String uid, Callback callback) {
        DocumentReference userRef = db.collection("users").document(uid);
        userRef.get().addOnSuccessListener(userDoc ->
                db.collection("posts").whereEqualTo("authorUid", uid).limit(MAX_POSTS).get()
                        .addOnSuccessListener(posts -> {
                            List<DocumentReference> refs = new ArrayList<>();
                            // Leaderboard entries live at leaderboards/{periodKey}/entries/{uid}; the period
                            // keys this user earned points in are recorded on their profile document.
                            for (String key : boardKeys(userDoc)) {
                                refs.add(db.collection("leaderboards").document(key).collection("entries").document(uid));
                            }
                            for (QueryDocumentSnapshot post : posts) {
                                refs.add(post.getReference());
                            }
                            refs.add(userRef); // last: if anything fails above, the record of what to clean up survives
                            deleteInChunks(refs, 0, callback);
                        })
                        .addOnFailureListener(callback::onError))
                .addOnFailureListener(callback::onError);
    }

    private static List<String> boardKeys(DocumentSnapshot userDoc) {
        List<String> keys = new ArrayList<>();
        Object raw = userDoc.get("boards");
        if (raw instanceof List) {
            for (Object o : (List<?>) raw) {
                if (o instanceof String) keys.add((String) o);
            }
        }
        return keys;
    }

    private void deleteInChunks(List<DocumentReference> refs, int from, Callback callback) {
        if (from >= refs.size()) {
            callback.onDone();
            return;
        }
        int to = Math.min(from + BATCH_SIZE, refs.size());
        WriteBatch batch = db.batch();
        for (DocumentReference ref : refs.subList(from, to)) {
            batch.delete(ref);
        }
        batch.commit()
                .addOnSuccessListener(v -> deleteInChunks(refs, to, callback))
                .addOnFailureListener(callback::onError);
    }
}
