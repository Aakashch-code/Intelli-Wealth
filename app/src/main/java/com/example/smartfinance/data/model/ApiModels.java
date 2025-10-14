package com.example.smartfinance.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiModels {

    // Gemini API Request Models
    public static class GeminiRequest {
        @SerializedName("contents")
        public List<Content> contents;

        @SerializedName("generationConfig")
        public GenerationConfig generationConfig;

        public GeminiRequest(List<Content> contents, GenerationConfig generationConfig) {
            this.contents = contents;
            this.generationConfig = generationConfig;
        }
    }

    public static class Content {
        @SerializedName("parts")
        public List<Part> parts;

        @SerializedName("role")
        public String role;

        public Content(List<Part> parts, String role) {
            this.parts = parts;
            this.role = role;
        }
    }

    public static class Part {
        @SerializedName("text")
        public String text;

        public Part(String text) {
            this.text = text;
        }
    }

    public static class GenerationConfig {
        @SerializedName("temperature")
        public double temperature = 0.7;

        @SerializedName("topK")
        public int topK = 40;

        @SerializedName("topP")
        public double topP = 0.95;

        @SerializedName("maxOutputTokens")
        public int maxOutputTokens = 1024;
    }

    // Gemini API Response Models
    public static class GeminiResponse {
        @SerializedName("candidates")
        public List<Candidate> candidates;

        @SerializedName("promptFeedback")
        public PromptFeedback promptFeedback;

        public String getResponseText() {
            if (candidates != null
                    && !candidates.isEmpty()
                    && candidates.get(0).content != null
                    && candidates.get(0).content.parts != null
                    && !candidates.get(0).content.parts.isEmpty()) {
                return candidates.get(0).content.parts.get(0).text;
            }
            return "Sorry, I couldn't generate a response. Please try again.";
        }    }

    public static class Candidate {
        @SerializedName("content")
        public Content content;

        @SerializedName("finishReason")
        public String finishReason;

        @SerializedName("safetyRatings")
        public List<SafetyRating> safetyRatings;
    }

    public static class PromptFeedback {
        @SerializedName("safetyRatings")
        public List<SafetyRating> safetyRatings;
    }

    public static class SafetyRating {
        @SerializedName("category")
        public String category;

        @SerializedName("probability")
        public String probability;
    }

    // Local transaction model for database
    public static class Transaction {
        @SerializedName("id")
        public int id;

        @SerializedName("type")
        public String type;

        @SerializedName("category")
        public String category;

        @SerializedName("amount")
        public double amount;

        @SerializedName("date")
        public String date;

        @SerializedName("payment_method")
        public String paymentMethod;

        @SerializedName("note")
        public String note;

        @SerializedName("timestamp")
        public long timestamp;

        public Transaction(int id, String type, String category, double amount,
                           String date, String paymentMethod, String note, long timestamp) {
            this.id = id;
            this.type = type;
            this.category = category;
            this.amount = amount;
            this.date = date;
            this.paymentMethod = paymentMethod;
            this.note = note;
            this.timestamp = timestamp;
        }
    }
}