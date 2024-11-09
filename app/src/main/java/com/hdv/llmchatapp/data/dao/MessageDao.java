package com.hdv.llmchatapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.hdv.llmchatapp.data.entity.Message;
import java.util.List;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    LiveData<List<Message>> getMessagesForChat(long chatId);

    @Insert
    void insertMessage(Message message);

    @Update
    void updateMessage(Message message);

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    void deleteMessagesForChat(long chatId);

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp DESC LIMIT :limit")
    List<Message> getRecentMessagesForChat(long chatId, int limit);
} 