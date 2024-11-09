package com.hdv.llmchatapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GemmaUiState implements UiState {
    private static final String START_TURN = "<start_of_turn>";
    private static final String END_TURN = "<end_of_turn>";
    private final List<ChatMessage> messages;
    private final Object lock = new Object();

    public GemmaUiState() {
        this(new ArrayList<>());
    }

    public GemmaUiState(List<ChatMessage> messages) {
        this.messages = new ArrayList<>(messages);
    }

    @Override
    public List<ChatMessage> getMessages() {
        synchronized (lock) {
            ArrayList<ChatMessage> processed = new ArrayList<>();
            for (ChatMessage message : messages) {
                String processedMessage = message.getRawMessage()
                        .replace(START_TURN + message.getAuthor() + "\n", "")
                        .replace(END_TURN, "");
                processed.add(new ChatMessage(
                        message.getId(),
                        processedMessage,
                        message.getAuthor(),
                        message.isLoading()
                ));
            }
            // Collections.reverse(processed);
            for (ChatMessage message : messages) {
                System.out.println("Message ID: " + message.getId() +
                                   ", Text: " + message.getRawMessage() +
                                   ", Author: " + message.getAuthor() +
                                   ", Is Loading: " + message.isLoading());
            }
            return processed;
        }
    }

    @Override
    public String getFullPrompt() {
        int startIndex = Math.max(0, messages.size() - 4);
        return messages.subList(startIndex, messages.size()).stream()
                .map(ChatMessage::getRawMessage)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String createLoadingMessage() {
        ChatMessage chatMessage = new ChatMessage("", Constants.MODEL_PREFIX);
        messages.add(chatMessage);
        return chatMessage.getId();
    }

    public void appendFirstMessage(String id, String text) {
        appendMessage(id, START_TURN + Constants.MODEL_PREFIX + "\n" + text, false);
    }

    @Override
    public void appendMessage(String id, String text, boolean done) {
        for (int i = 0; i < messages.size(); i++) {
            ChatMessage message = messages.get(i);
            if (message.getId().equals(id)) {
                String newText = done ? 
                    message.getRawMessage() + text + END_TURN :
                    message.getRawMessage() + text;
                messages.set(i, new ChatMessage(id, newText, message.getAuthor(), false));
                break;
            }
        }
    }

    @Override
    public String addMessage(String text, String author) {
        ChatMessage chatMessage = new ChatMessage(
                START_TURN + author + "\n" + text + END_TURN,
                author
        );
        messages.add(chatMessage);
        return chatMessage.getId();
    }
} 