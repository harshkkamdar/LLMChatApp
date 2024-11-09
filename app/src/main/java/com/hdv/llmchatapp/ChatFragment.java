package com.hdv.llmchatapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.hdv.llmchatapp.data.entity.Message;

public class ChatFragment extends Fragment {
    private static final String ARG_CHAT_ID = "chat_id";
    
    private ChatViewModel viewModel;
    private EditText messageInput;
    private ImageButton sendButton;
    private ChatAdapter chatAdapter;

    public static ChatFragment newInstance(long chatId) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CHAT_ID, chatId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create ViewModel using our custom Factory
        ChatViewModel.Factory factory = new ChatViewModel.Factory(requireActivity().getApplication());
        viewModel = new ViewModelProvider(this, factory).get(ChatViewModel.class);
        
        long chatId = getArguments().getLong(ARG_CHAT_ID);
        viewModel.setCurrentChat(chatId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, 
                           @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup toolbar with back button
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        messageInput = view.findViewById(R.id.message_input);
        sendButton = view.findViewById(R.id.send_button);
        RecyclerView recyclerView = view.findViewById(R.id.chat_recycler_view);

        chatAdapter = new ChatAdapter();
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            chatAdapter.submitList(messages);
            if (!messages.isEmpty()) {
                recyclerView.smoothScrollToPosition(messages.size() - 1);
            }
        });

        viewModel.isInputEnabled().observe(getViewLifecycleOwner(), enabled -> {
            messageInput.setEnabled(enabled);
            sendButton.setEnabled(enabled);
            System.out.println("Input enabled changed to: " + enabled); // Debug log
            
            if (!enabled) {
                // Set a timeout to re-enable input if it stays disabled too long
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (!messageInput.isEnabled()) {
                        viewModel.setInputEnabled(true);
                        System.out.println("Force enabling input after timeout");
                    }
                }, 10000); // 10 second timeout
            }
        });

        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                viewModel.sendMessage(message);
                messageInput.setText("");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Reset the back button when leaving the fragment
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
} 