package com.hdv.llmchatapp.ui;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.hdv.llmchatapp.data.ChatRepository;
import com.hdv.llmchatapp.data.entity.Chat;
import java.util.List;

public class ChatListViewModel extends AndroidViewModel {
    private final ChatRepository repository;
    private final LiveData<List<Chat>> allChats;

    public ChatListViewModel(Application application) {
        super(application);
        repository = new ChatRepository(application);
        allChats = repository.getAllChats();
    }

    public LiveData<List<Chat>> getAllChats() {
        return allChats;
    }

    public void createNewChat(String title, ChatRepository.OnChatCreatedListener listener) {
        Chat chat = new Chat(title);
        repository.insertChat(chat, listener);
    }

    public void updateChat(Chat chat) {
        chat.setUpdatedAt(System.currentTimeMillis());
        repository.updateChat(chat);
    }

    public void deleteChat(Chat chat) {
        repository.deleteChat(chat);
    }
} 