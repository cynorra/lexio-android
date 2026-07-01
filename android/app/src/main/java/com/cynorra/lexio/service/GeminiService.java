package com.cynorra.lexio.service;

public class GeminiService {
    public static class VerdictScore {
        private final String label;
        private final int value;

        public VerdictScore(String label, int value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public int getValue() {
            return value;
        }
    }
}
