package com.hdv.llmchatapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ChatUiState implements UiState {
    private final List<ChatMessage> messages;

    public ChatUiState() {
        this(new ArrayList<>());
    }

    public ChatUiState(List<ChatMessage> messages) {
        this.messages = new ArrayList<>(messages);
    }

    @Override
    public List<ChatMessage> getMessages() {
        ArrayList<ChatMessage> reversed = new ArrayList<>(messages);
        Collections.reverse(reversed);
        return reversed;
    }

    @Override
    public String getFullPrompt() {
        return messages.stream()
                .map(ChatMessage::getRawMessage)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String createLoadingMessage() {
        ChatMessage chatMessage = new ChatMessage("", Constants.MODEL_PREFIX, true);
        messages.add(chatMessage);
        return chatMessage.getId();
    }

    @Override
    public void appendMessage(String id, String text, boolean done) {
        for (int i = 0; i < messages.size(); i++) {
            ChatMessage message = messages.get(i);
            if (message.getId().equals(id)) {
                messages.set(i, new ChatMessage(
                    id,
                    text,
                    message.getAuthor(),
                    !done
                ));
                break;
            }
        }
    }

    @Override
    public String addMessage(String text, String author) {
        ChatMessage chatMessage = new ChatMessage(text, author);
        messages.add(chatMessage);
        return chatMessage.getId();
    }
} 