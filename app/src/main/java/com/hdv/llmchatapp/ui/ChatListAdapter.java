package com.hdv.llmchatapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.hdv.llmchatapp.R;
import com.hdv.llmchatapp.data.entity.Chat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatListAdapter extends ListAdapter<Chat, ChatListAdapter.ChatViewHolder> {
    private final ChatClickListener listener;
    private static final SimpleDateFormat dateFormat = 
        new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());

    public ChatListAdapter(ChatClickListener listener) {
        super(new ChatDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleView;
        private final TextView timestampView;
        private final ImageButton editButton;
        private final ImageButton deleteButton;

        ChatViewHolder(View itemView, ChatClickListener listener) {
            super(itemView);
            titleView = itemView.findViewById(R.id.chat_title);
            timestampView = itemView.findViewById(R.id.chat_timestamp);
            editButton = itemView.findViewById(R.id.btn_edit);
            deleteButton = itemView.findViewById(R.id.btn_delete);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onChatClick(getItem(position));
                }
            });

            editButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(getItem(position));
                }
            });

            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(getItem(position));
                }
            });
        }

        void bind(Chat chat) {
            titleView.setText(chat.getTitle());
            timestampView.setText(dateFormat.format(new Date(chat.getUpdatedAt())));
        }
    }

    public interface ChatClickListener {
        void onChatClick(Chat chat);
        void onEditClick(Chat chat);
        void onDeleteClick(Chat chat);
    }

    private static class ChatDiffCallback extends DiffUtil.ItemCallback<Chat> {
        @Override
        public boolean areItemsTheSame(@NonNull Chat oldItem, @NonNull Chat newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Chat oldItem, @NonNull Chat newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                   oldItem.getUpdatedAt() == newItem.getUpdatedAt();
        }
    }
} 