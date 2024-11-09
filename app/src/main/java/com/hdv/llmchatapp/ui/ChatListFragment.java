package com.hdv.llmchatapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.hdv.llmchatapp.MainActivity;
import com.hdv.llmchatapp.R;
import com.hdv.llmchatapp.data.entity.Chat;

import java.util.Objects;

public class ChatListFragment extends Fragment implements ChatListAdapter.ChatClickListener {
    private ChatListViewModel viewModel;
    private ChatListAdapter adapter;
    private TextView emptyView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ChatListViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, 
                           @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.chat_list);
        FloatingActionButton fab = view.findViewById(R.id.fab_new_chat);
        emptyView = view.findViewById(R.id.empty_view);

        adapter = new ChatListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel.getAllChats().observe(getViewLifecycleOwner(), chats -> {
            adapter.submitList(chats);
            emptyView.setVisibility(chats.isEmpty() ? View.VISIBLE : View.GONE);
        });

        fab.setOnClickListener(v -> showNewChatDialog());
    }

    private void showNewChatDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_chat_title, null);
        TextInputEditText titleInput = dialogView.findViewById(R.id.title_input);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.new_chat)
                .setView(dialogView)
                .setPositiveButton(R.string.create, (dialog, which) -> {
                    String title = Objects.requireNonNull(titleInput.getText()).toString().trim();
                    if (!title.isEmpty()) {
                        // Navigate to ChatFragment with chatId
                        viewModel.createNewChat(title, this::navigateToChatFragment);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    @Override
    public void onChatClick(Chat chat) {
        navigateToChatFragment(chat.getId());
    }

    @Override
    public void onEditClick(Chat chat) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_chat_title, null);
        TextInputEditText titleInput = dialogView.findViewById(R.id.title_input);
        titleInput.setText(chat.getTitle());

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.edit_chat)
                .setView(dialogView)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String newTitle = Objects.requireNonNull(titleInput.getText()).toString().trim();
                    if (!newTitle.isEmpty()) {
                        chat.setTitle(newTitle);
                        viewModel.updateChat(chat);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    @Override
    public void onDeleteClick(Chat chat) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_chat)
                .setMessage(R.string.delete_chat_confirmation)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    viewModel.deleteChat(chat);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void navigateToChatFragment(long chatId) {
        ((MainActivity) requireActivity()).showChatFragment(chatId);
    }
} 