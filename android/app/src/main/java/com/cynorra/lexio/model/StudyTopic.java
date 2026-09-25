package com.cynorra.lexio.model;

import java.util.ArrayList;
import java.util.List;

public class StudyTopic {
    private final String id;
    private final String title;
    private final String category; // e.g. "Constitutional Law"
    private final String shortDesc;
    private final String fullContent; // simple HTML
    private final int readTimeMinutes;
    private final String reference; // provision / case the lesson is anchored on
    private final List<DailyQuizQuestion> questions;

    public StudyTopic(String id, String title, String category, String shortDesc, String fullContent,
                      int readTimeMinutes, String reference, List<DailyQuizQuestion> questions) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.shortDesc = shortDesc;
        this.fullContent = fullContent;
        this.readTimeMinutes = readTimeMinutes;
        this.reference = reference;
        this.questions = questions != null ? questions : new ArrayList<>();
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getShortDesc() { return shortDesc; }
    public String getFullContent() { return fullContent; }
    public int getReadTimeMinutes() { return readTimeMinutes; }
    public String getReference() { return reference; }
    public List<DailyQuizQuestion> getQuestions() { return questions; }
}
