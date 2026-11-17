package com.cynorra.lexio.model;

import com.cynorra.lexio.R;

import java.util.ArrayList;
import java.util.List;

public class BadgeData {
    public static class Badge {
        private final String id;
        private final String icon;
        private final String title;
        private final String description;
        private final boolean isUnlocked;

        public Badge(String id, String icon, String title, String description, boolean isUnlocked) {
            this.id = id;
            this.icon = icon;
            this.title = title;
            this.description = description;
            this.isUnlocked = isUnlocked;
        }

        public String getId() { return id; }
        public String getIcon() { return icon; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public boolean isUnlocked() { return isUnlocked; }
    }

    public static List<Badge> getAllBadges(android.content.Context ctx, int userScore, int dailyStreak, int casesSolved) {
        List<Badge> list = new ArrayList<>();
        
        list.add(new Badge(
            "b_first_win",
            "⚖️",
            ctx.getString(R.string.badge_first_win_title),
            ctx.getString(R.string.badge_first_win_desc),
            casesSolved >= 1
        ));

        list.add(new Badge(
            "b_streak_3",
            "🔥",
            ctx.getString(R.string.badge_streak_3_title),
            ctx.getString(R.string.badge_streak_3_desc),
            dailyStreak >= 3
        ));

        list.add(new Badge(
            "b_score_500",
            "🛡️",
            ctx.getString(R.string.badge_score_500_title),
            ctx.getString(R.string.badge_score_500_desc),
            userScore >= 500
        ));

        list.add(new Badge(
            "b_supreme",
            "👑",
            ctx.getString(R.string.badge_supreme_title),
            ctx.getString(R.string.badge_supreme_desc),
            userScore >= 1000
        ));

        return list;
    }
}
