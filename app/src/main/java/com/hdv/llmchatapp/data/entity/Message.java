package com.hdv.llmchatapp.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages",
        foreignKeys = @ForeignKey(entity = Chat.class,
                                parentColumns = "id",
                                childColumns = "chatId",
                                onDelete = ForeignKey.CASCADE))
public class Message {
    @PrimaryKey
    @NonNull
    private String id;
    private long chatId;
    private String content;
    private String author;
    private long timestamp;

    public Message(@NonNull String id, long chatId, String content, String author) {
        this.id = id;
        this.chatId = chatId;
        this.content = content;
        this.author = author;
        this.timestamp = System.currentTimeMillis();
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public long getChatId() { return chatId; }
    public void setChatId(long chatId) { this.chatId = chatId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
} 