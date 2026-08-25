package com.cynorra.lexio.model;

public class FeedPost {
    private String docId;
    private int id;
    private String authorName;
    private String authorRole; // e.g. "Avukat", "Stajyer", "Öğrenci"
    private String timestamp;
    private String content;
    private int likesCount;
    private int commentsCount;

    public FeedPost() {
        // Required empty constructor for Firestore
    }

    public FeedPost(int id, String authorName, String authorRole, String timestamp, String content, int likesCount, int commentsCount) {
        this.id = id;
        this.authorName = authorName;
        this.authorRole = authorRole;
        this.timestamp = timestamp;
        this.content = content;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
    }

    public String getDocId() { return docId; }
    public void setDocId(String docId) { this.docId = docId; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorRole() { return authorRole; }
    public void setAuthorRole(String authorRole) { this.authorRole = authorRole; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }

    public int getCommentsCount() { return commentsCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }
}

