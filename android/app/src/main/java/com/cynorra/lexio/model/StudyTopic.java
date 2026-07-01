package com.cynorra.lexio.model;

public class StudyTopic {
    private final String id;
    private final String title;
    private final String category; // e.g. "Ceza Hukuku Genel", "Ceza Hukuku Özel"
    private final String shortDesc;
    private final String fullContent;
    private final int readTimeMinutes;

    public StudyTopic(String id, String title, String category, String shortDesc, String fullContent, int readTimeMinutes) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.shortDesc = shortDesc;
        this.fullContent = fullContent;
        this.readTimeMinutes = readTimeMinutes;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getShortDesc() { return shortDesc; }
    public String getFullContent() { return fullContent; }
    public int getReadTimeMinutes() { return readTimeMinutes; }
}
