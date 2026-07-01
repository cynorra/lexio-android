package com.cynorra.lexio.model;

public class FeedPost {
    private int id;
    private String authorName;
    private String authorRole; // e.g. "Avukat", "Stajyer", "Öğrenci"
    private String timestamp;
    private String content;
    private int likesCount;
    private int commentsCount;

    public FeedPost(int id, String authorName, String authorRole, String timestamp, String content, int likesCount, int commentsCount) {
        this.id = id;
        this.authorName = authorName;
        this.authorRole = authorRole;
        this.timestamp = timestamp;
        this.content = content;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
    }

    public int getId() { return id; }
    public String getAuthorName() { return authorName; }
    public String getAuthorRole() { return authorRole; }
    public String getTimestamp() { return timestamp; }
    public String getContent() { return content; }
    public int getLikesCount() { return likesCount; }
    public int getCommentsCount() { return commentsCount; }
}
