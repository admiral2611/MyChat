package com.admiral26.mychat.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admiral26.mychat.databinding.ItemReceivedMessageBinding
import com.admiral26.mychat.databinding.ItemSendMessageBinding
import com.admiral26.mychat.model.ChatMessage

class MultiAdapter(
    private val senderId: String? = null,
    private val receiverProfileImage: Bitmap? = null,
    private val listMessage: ArrayList<ChatMessage> = ArrayList()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listChat = mutableListOf<ChatMessage>()
    companion object {
        private const val VIEW_TYPE_SEND = 1
        private const val VIEW_TYPE_RECEIVE = 2
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<ChatMessage>) {
        listChat.clear()
        listChat.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (listMessage[position].senderId == senderId) {
            VIEW_TYPE_SEND
        } else {
            VIEW_TYPE_RECEIVE
        }
    }

    inner class SendViewHolder(private val binding: ItemSendMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            binding.textMessage.text = message.text
            binding.textTime.text = message.dataTime
        }
    }

    inner class ReceiverMessageViewHolder(private val binding: ItemReceivedMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage, bitmap: Bitmap?) {
            binding.textMessage.text = message.text
            binding.timeMessage.text = message.dataTime
            bitmap?.let { binding.imageProfile.setImageBitmap(it) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SEND) {
            SendViewHolder(
                ItemSendMessageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        } else {
            ReceiverMessageViewHolder(
                ItemReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun getItemCount() = listMessage.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SendViewHolder -> holder.bind(listMessage[position])
            is ReceiverMessageViewHolder -> holder.bind(listMessage[position], receiverProfileImage)
        }
    }
}