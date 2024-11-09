package com.hdv.llmchatapp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.hdv.llmchatapp.data.ChatRepository;
import com.hdv.llmchatapp.data.entity.Message;

import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class ChatViewModel extends ViewModel {
    private final InferenceModel inferenceModel;
    private final AtomicReference<GemmaUiState> uiState;
    private final MutableLiveData<Boolean> inputEnabled;
    private final ExecutorService executorService;
    private final ChatRepository repository;
    private long currentChatId;
    private LiveData<List<Message>> messages;

    public ChatViewModel(Context context, InferenceModel inferenceModel) {
        this.inferenceModel = inferenceModel;
        this.uiState = new AtomicReference<>(new GemmaUiState());
        this.inputEnabled = new MutableLiveData<>(true);
        this.executorService = Executors.newSingleThreadExecutor();
        this.repository = new ChatRepository((Application) context.getApplicationContext());
    }

    public void setCurrentChat(long chatId) {
        this.currentChatId = chatId;
        this.messages = repository.getMessagesForChat(chatId);
    }

    public LiveData<List<Message>> getMessages() {
        return messages;
    }

    @SuppressLint("NewApi")
    public void sendMessage(String userMessage) {
        executorService.execute(() -> {
            // Save user message to database
            Message userMsg = new Message(
                java.util.UUID.randomUUID().toString(),
                currentChatId,
                userMessage,
                Constants.USER_PREFIX
            );
            repository.insertMessage(userMsg);

            // Update UI state
            GemmaUiState currentState = uiState.get();
            currentState.addMessage(userMessage, Constants.USER_PREFIX);
            String currentMessageId = currentState.createLoadingMessage();
            
            // Create loading message in database
            Message modelMsg = new Message(
                currentMessageId,
                currentChatId,
                "",
                Constants.MODEL_PREFIX
            );
            repository.insertMessage(modelMsg);

            setInputEnabled(false);

            try {
                String fullPrompt = currentState.getFullPrompt();
                inferenceModel.generateResponseAsync(fullPrompt);
                
                inferenceModel.getPartialResults().subscribe(new Flow.Subscriber<>() {
                    private int index = 0;
                    private StringBuilder responseBuilder = new StringBuilder();
                    
                    @Override
                    public void onSubscribe(Flow.Subscription subscription) {
                        subscription.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(InferenceModel.Pair<String, Boolean> result) {
                        responseBuilder.append(result.first);
                        
                        // Update UI state
                        if (index == 0) {
                            currentState.appendFirstMessage(currentMessageId, result.first);
                        } else {
                            currentState.appendMessage(currentMessageId, result.first, result.second);
                        }

                        // Update database
                        Message updatedMsg = new Message(
                            currentMessageId,
                            currentChatId,
                            responseBuilder.toString().trim(),
                            Constants.MODEL_PREFIX
                        );
                        repository.updateMessage(updatedMsg);

                        if (result.second) {
                            setInputEnabled(true);
                        }
                        index++;
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        currentState.addMessage(throwable.getLocalizedMessage(), Constants.MODEL_PREFIX);
                        setInputEnabled(true);
                    }

                    @Override
                    public void onComplete() {}
                });
            } catch (Exception e) {
                currentState.addMessage(e.getLocalizedMessage(), Constants.MODEL_PREFIX);
                setInputEnabled(true);
            }
        });
    }

    public void setInputEnabled(boolean isEnabled) {
        inputEnabled.postValue(isEnabled);
    }

    public GemmaUiState getUiState() {
        return uiState.get();
    }

    public LiveData<Boolean> isInputEnabled() {
        return inputEnabled;
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final Context context;

        public Factory(Context context) {
            this.context = context;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new ChatViewModel(context, InferenceModel.getInstance(context));
        }
    }
} 