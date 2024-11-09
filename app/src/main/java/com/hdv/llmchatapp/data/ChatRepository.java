package com.hdv.llmchatapp.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.hdv.llmchatapp.data.dao.ChatDao;
import com.hdv.llmchatapp.data.dao.MessageDao;
import com.hdv.llmchatapp.data.entity.Chat;
import com.hdv.llmchatapp.data.entity.Message;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatRepository {
    private final ChatDao chatDao;
    private final MessageDao messageDao;
    private final ExecutorService executorService;

    public ChatRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        chatDao = db.chatDao();
        messageDao = db.messageDao();
        executorService = Executors.newFixedThreadPool(4);
    }

    public LiveData<List<Chat>> getAllChats() {
        return chatDao.getAllChats();
    }

    public LiveData<Chat> getChatById(long chatId) {
        return chatDao.getChatById(chatId);
    }

    public LiveData<List<Message>> getMessagesForChat(long chatId) {
        return messageDao.getMessagesForChat(chatId);
    }

    public void insertChat(Chat chat, OnChatCreatedListener listener) {
        executorService.execute(() -> {
            long chatId = chatDao.insertChat(chat);
            listener.onChatCreated(chatId);
        });
    }

    public void updateChat(Chat chat) {
        executorService.execute(() -> chatDao.updateChat(chat));
    }

    public void insertMessage(Message message) {
        executorService.execute(() -> messageDao.insertMessage(message));
    }

    public void updateMessage(Message message) {
        executorService.execute(() -> messageDao.updateMessage(message));
    }

    public void getRecentMessagesForChat(long chatId, int limit, OnMessagesLoadedListener listener) {
        executorService.execute(() -> {
            List<Message> messages = messageDao.getRecentMessagesForChat(chatId, limit);
            listener.onMessagesLoaded(messages);
        });
    }

    public void deleteChat(Chat chat) {
        executorService.execute(() -> {
            messageDao.deleteMessagesForChat(chat.getId());
            chatDao.deleteChat(chat);
        });
    }

    public interface OnChatCreatedListener {
        void onChatCreated(long chatId);
    }

    public interface OnMessagesLoadedListener {
        void onMessagesLoaded(List<Message> messages);
    }

    // Synchronous version that returns the messages directly
    public List<Message> getRecentMessagesForChat(long chatId, int limit) {
        return messageDao.getRecentMessagesForChat(chatId, limit);
    }
} 