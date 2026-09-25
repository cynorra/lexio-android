package com.cynorra.lexio.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Everything about a user that must survive process death and follow them across devices. */
public final class UserProgress {
    public final int careerScore;
    public final int dailyStreak;
    public final String lastQuizDate; // yyyy-MM-dd, null if never completed
    public final String instagramHandle; // null if not set
    /** Best simulation score per case id; a case counts as solved once it has an entry. */
    public final Map<Integer, Integer> caseBest;

    public UserProgress(int careerScore, int dailyStreak, String lastQuizDate,
                        String instagramHandle, Map<Integer, Integer> caseBest) {
        this.careerScore = careerScore;
        this.dailyStreak = dailyStreak;
        this.lastQuizDate = lastQuizDate;
        this.instagramHandle = instagramHandle;
        this.caseBest = caseBest != null
                ? Collections.unmodifiableMap(new HashMap<>(caseBest))
                : Collections.<Integer, Integer>emptyMap();
    }

    public static UserProgress empty() {
        return new UserProgress(0, 0, null, null, null);
    }

    /**
     * Combines progress from two sources (local cache and server). Scores only ever grow,
     * so the larger value wins; the streak belongs to whichever side completed a quiz last.
     */
    public static UserProgress merge(UserProgress a, UserProgress b) {
        if (a == null) return b;
        if (b == null) return a;

        Map<Integer, Integer> best = new HashMap<>(a.caseBest);
        for (Map.Entry<Integer, Integer> e : b.caseBest.entrySet()) {
            Integer existing = best.get(e.getKey());
            if (existing == null || e.getValue() > existing) best.put(e.getKey(), e.getValue());
        }

        boolean bIsNewer = b.lastQuizDate != null
                && (a.lastQuizDate == null || b.lastQuizDate.compareTo(a.lastQuizDate) > 0);
        UserProgress quizSource = bIsNewer ? b : a;
        String insta = a.instagramHandle != null ? a.instagramHandle : b.instagramHandle;

        return new UserProgress(Math.max(a.careerScore, b.careerScore),
                quizSource.dailyStreak, quizSource.lastQuizDate, insta, best);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProgress)) return false;
        UserProgress p = (UserProgress) o;
        return careerScore == p.careerScore && dailyStreak == p.dailyStreak
                && Objects.equals(lastQuizDate, p.lastQuizDate)
                && Objects.equals(instagramHandle, p.instagramHandle)
                && caseBest.equals(p.caseBest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(careerScore, dailyStreak, lastQuizDate, instagramHandle, caseBest);
    }
}
