package com.cynorra.lexio.model;

import java.util.List;

public class Case {
    public static class Choice {
        private final String text;
        private final String aiResponse;
        private final int scoreEffect; // e.g., points for this choice

        public Choice(String text, String aiResponse, int scoreEffect) {
            this.text = text;
            this.aiResponse = aiResponse;
            this.scoreEffect = scoreEffect;
        }

        public String getText() { return text; }
        public String getAiResponse() { return aiResponse; }
        public int getScoreEffect() { return scoreEffect; }
    }

    public static class TrialStep {
        private final String stepName;
        private final String prompt;
        private final List<Choice> defenseChoices;
        private final List<Choice> prosecutionChoices;

        public TrialStep(String stepName, String prompt, List<Choice> defenseChoices, List<Choice> prosecutionChoices) {
            this.stepName = stepName;
            this.prompt = prompt;
            this.defenseChoices = defenseChoices;
            this.prosecutionChoices = prosecutionChoices;
        }

        public String getStepName() { return stepName; }
        public String getPrompt() { return prompt; }
        public List<Choice> getDefenseChoices() { return defenseChoices; }
        public List<Choice> getProsecutionChoices() { return prosecutionChoices; }
    }

    private final int id;
    private final String tag;
    private final String tagLabel;
    private final String title;
    private final String preview;
    private final String difficulty;
    private final String duration;
    private final String ref;
    private final String full;
    private final List<String> evidence;
    private final List<TrialStep> trialSteps;
    private final String finalVerdictDefenseWin;
    private final String finalVerdictDefenseLose;
    private final String jurisdiction; // "TR", "US", "UK", "DE"

    public Case(int id, String tag, String tagLabel, String title, String preview,
                String difficulty, String duration, String ref, String full,
                List<String> evidence, List<TrialStep> trialSteps,
                String finalVerdictDefenseWin, String finalVerdictDefenseLose,
                String jurisdiction) {
        this.id = id;
        this.tag = tag;
        this.tagLabel = tagLabel;
        this.title = title;
        this.preview = preview;
        this.difficulty = difficulty;
        this.duration = duration;
        this.ref = ref;
        this.full = full;
        this.evidence = evidence;
        this.trialSteps = trialSteps;
        this.finalVerdictDefenseWin = finalVerdictDefenseWin;
        this.finalVerdictDefenseLose = finalVerdictDefenseLose;
        this.jurisdiction = jurisdiction != null ? jurisdiction : "TR";
    }

    public int getId() { return id; }
    public String getTag() { return tag; }
    public String getTagLabel() { return tagLabel; }
    public String getTitle() { return title; }
    public String getPreview() { return preview; }
    public String getDifficulty() { return difficulty; }
    public String getDuration() { return duration; }
    public String getRef() { return ref; }
    public String getFull() { return full; }
    public List<String> getEvidence() { return evidence; }
    public List<TrialStep> getTrialSteps() { return trialSteps; }
    public String getFinalVerdictDefenseWin() { return finalVerdictDefenseWin; }
    public String getFinalVerdictDefenseLose() { return finalVerdictDefenseLose; }
    public String getJurisdiction() { return jurisdiction; }
}
