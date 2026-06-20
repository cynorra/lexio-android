package com.lexio.app.model;

public class LeaderboardEntry {
    private final int rank;
    private final String username;
    private final int score;
    private final boolean isCurrentUser;

    public LeaderboardEntry(int rank, String username, int score, boolean isCurrentUser) {
        this.rank = rank;
        this.username = username;
        this.score = score;
        this.isCurrentUser = isCurrentUser;
    }

    public int getRank() { return rank; }
    public String getUsername() { return username; }
    public int getScore() { return score; }
    public boolean isCurrentUser() { return isCurrentUser; }
}
