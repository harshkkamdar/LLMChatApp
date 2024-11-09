package com.hdv.llmchatapp;

import android.annotation.SuppressLint;
import android.content.Context;
import com.google.mediapipe.tasks.genai.llminference.LlmInference;
import java.io.File;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class InferenceModel {
    private static final String MODEL_PATH = "/data/local/tmp/model.bin";
    private static InferenceModel instance;
    private final LlmInference llmInference;
    private SubmissionPublisher<Pair<String, Boolean>> partialResultsPublisher;

    @SuppressLint("NewApi")
    private InferenceModel(Context context) {
        if (!new File(MODEL_PATH).exists()) {
            throw new IllegalArgumentException("Model not found at path: " + MODEL_PATH);
        }

        partialResultsPublisher = new SubmissionPublisher<>();

        LlmInference.LlmInferenceOptions options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(MODEL_PATH)
                .setMaxTokens(1024)
                .setResultListener((partialResult, done) -> {
                    if (partialResultsPublisher != null && !partialResultsPublisher.isClosed()) {
                        partialResultsPublisher.submit(new Pair<>(partialResult, done));
                    }
                })
                .build();

        llmInference = LlmInference.createFromOptions(context, options);
    }

    public static synchronized InferenceModel getInstance(Context context) {
        if (instance == null) {
            instance = new InferenceModel(context);
        }
        return instance;
    }

    @SuppressLint("NewApi")
    public void generateResponseAsync(String prompt) {
        if (partialResultsPublisher != null) {
            partialResultsPublisher.close();
        }
        partialResultsPublisher = new SubmissionPublisher<>();
        
        llmInference.generateResponseAsync(prompt);
    }

    public Flow.Publisher<Pair<String, Boolean>> getPartialResults() {
        return partialResultsPublisher;
    }

    public static class Pair<F, S> {
        public final F first;
        public final S second;

        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
    }
} 