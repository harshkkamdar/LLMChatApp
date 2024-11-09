package com.hdv.llmchatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.hdv.llmchatapp.data.entity.Message;

public class ChatAdapter extends ListAdapter<Message, ChatAdapter.MessageViewHolder> {

    protected ChatAdapter() {
        super(new MessageDiffCallback());
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView authorView;
        private final TextView messageView;
        private final View messageContainer;

        MessageViewHolder(View itemView) {
            super(itemView);
            authorView = itemView.findViewById(R.id.text_author);
            messageView = itemView.findViewById(R.id.text_message);
            messageContainer = itemView.findViewById(R.id.message_container);
        }

        void bind(Message message) {
            boolean isUser = Constants.USER_PREFIX.equals(message.getAuthor());
            
            authorView.setText(isUser ? R.string.user_label : R.string.model_label);
            messageView.setText(message.getContent());
            
            messageContainer.setBackgroundResource(
                isUser ? R.drawable.bg_message_user : R.drawable.bg_message_model);
            
            ((LinearLayout.LayoutParams) messageContainer.getLayoutParams()).gravity =
                isUser ? android.view.Gravity.END : android.view.Gravity.START;
        }
    }

    private static class MessageDiffCallback extends DiffUtil.ItemCallback<Message> {
        @Override
        public boolean areItemsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
            return oldItem.getContent().equals(newItem.getContent());
        }
    }
} 