package com.cynorra.lexio.model;

import java.util.List;

public class DailyQuizQuestion {
    private final String id;
    private final String question;
    private final List<String> options;
    private final int correctIndex;
    private final String explanation;
    private final String jurisdiction; // "TR", "US", "UK", "DE"

    public DailyQuizQuestion(String id, String question, List<String> options, int correctIndex, String explanation, String jurisdiction) {
        this.id = id;
        this.question = question;
        this.options = options;
        this.correctIndex = correctIndex;
        this.explanation = explanation;
        this.jurisdiction = jurisdiction;
    }

    public String getId() { return id; }
    public String getQuestion() { return question; }
    public List<String> getOptions() { return options; }
    public int getCorrectIndex() { return correctIndex; }
    public String getExplanation() { return explanation; }
    public String getJurisdiction() { return jurisdiction; }
}
