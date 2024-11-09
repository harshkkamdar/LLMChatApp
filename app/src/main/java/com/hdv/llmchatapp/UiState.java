package com.hdv.llmchatapp;

import java.util.List;

public interface UiState {
    List<ChatMessage> getMessages();
    String getFullPrompt();
    String createLoadingMessage();
    void appendMessage(String id, String text, boolean done);
    String addMessage(String text, String author);
} 