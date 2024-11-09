package com.hdv.llmchatapp;

import java.util.UUID;

public class ChatMessage {
    private String id;
    private  String rawMessage;
    private  String author;
    private  boolean isLoading;

    public ChatMessage(String rawMessage, String author) {
        this(UUID.randomUUID().toString(), rawMessage, author, false);
    }

    public ChatMessage(String id, String rawMessage, String author, boolean isLoading) {
        this.id = id;
        this.rawMessage = rawMessage;
        this.author = author;
        this.isLoading = isLoading;
    }

    public ChatMessage(String id, String modelPrefix, boolean isLoading) {
        this(id.isEmpty() ? UUID.randomUUID().toString() : id, "", modelPrefix, isLoading);
        this.rawMessage = "";
        this.author = modelPrefix;
        this.isLoading = isLoading;
    }

    public String getId() {
        return id;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isFromUser() {
        return author.equals(Constants.USER_PREFIX);
    }

    public String getMessage() {
        return rawMessage.trim();
    }
} 