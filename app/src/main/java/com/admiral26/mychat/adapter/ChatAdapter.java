package com.admiral26.mychat.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.admiral26.mychat.databinding.ItemReceivedMessageBinding;
import com.admiral26.mychat.databinding.ItemSendMessageBinding;
import com.admiral26.mychat.model.Chat;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_SEND = 1;
    public static final int VIEW_TYPE_RECEIVE = 2;
    private final List<Chat> chatMessages;
    private final String senderId;
    private final Bitmap receiverProfileImage;

    public ChatAdapter(List<Chat> chatMessages, String senderId, Bitmap receiverProfileImage) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
        this.receiverProfileImage = receiverProfileImage;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SEND) {
            return new SendMessageViewHolder(
                    ItemSendMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false));
        } else {
            return new RecieverMessageViewHolder(
                    ItemReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SEND) {
            ((SendMessageViewHolder) holder).setData(chatMessages.get(position));
        } else {
            ((RecieverMessageViewHolder) holder).setData(chatMessages.get(position), receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderId.equals(senderId)) {
            return VIEW_TYPE_SEND;
        } else {
            return VIEW_TYPE_RECEIVE;
        }
    }

    static class SendMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemSendMessageBinding binding;

        SendMessageViewHolder(ItemSendMessageBinding itemSendMessageBinding) {
            super(itemSendMessageBinding.getRoot());
            binding = itemSendMessageBinding;
        }

        void setData(Chat chatMessage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textTime.setText(chatMessage.dataTime);
        }
    }

    private static class RecieverMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemReceivedMessageBinding binding;

        RecieverMessageViewHolder(ItemReceivedMessageBinding itemReceivedMessageBinding) {
            super(itemReceivedMessageBinding.getRoot());
            binding = itemReceivedMessageBinding;
        }

        void setData(Chat chatMessage, Bitmap receiverProfileImage) {
            binding.textMessage.setText(chatMessage.message);
            binding.timeMessage.setText(chatMessage.dataTime);
            binding.imageProfile.setImageBitmap(receiverProfileImage);
        }

    }
}
