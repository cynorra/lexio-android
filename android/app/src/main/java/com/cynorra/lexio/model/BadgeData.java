package com.cynorra.lexio.model;

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

    public static List<Badge> getAllBadges(int userScore, int dailyStreak, int casesSolved) {
        List<Badge> list = new ArrayList<>();
        
        list.add(new Badge(
            "b_first_win",
            "⚖️",
            "İlk Zafer",
            "İlk mahkeme simülasyonunu başarıyla tamamla.",
            casesSolved >= 1
        ));

        list.add(new Badge(
            "b_streak_3",
            "🔥",
            "Seri Üstadı",
            "3 gün üst üste günlük testleri tamamla.",
            dailyStreak >= 3
        ));

        list.add(new Badge(
            "b_score_500",
            "🛡️",
            "Kusursuz Müdafi",
            "Toplam 500 kariyer puanına ulaş.",
            userScore >= 500
        ));

        list.add(new Badge(
            "b_supreme",
            "👑",
            "Yargıtay Hakimi",
            "1000 puan barajını aşarak en üst makama yüksel.",
            userScore >= 1000
        ));

        return list;
    }
}
