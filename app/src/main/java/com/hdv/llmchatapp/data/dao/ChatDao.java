package com.hdv.llmchatapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.hdv.llmchatapp.data.entity.Chat;
import java.util.List;

@Dao
public interface ChatDao {
    @Query("SELECT * FROM chats ORDER BY updatedAt DESC")
    LiveData<List<Chat>> getAllChats();

    @Insert
    long insertChat(Chat chat);

    @Update
    void updateChat(Chat chat);

    @Delete
    void deleteChat(Chat chat);

    @Query("SELECT * FROM chats WHERE id = :chatId")
    LiveData<Chat> getChatById(long chatId);
} 