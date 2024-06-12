package com.example.fastcampusbasic.Part2.chapter6.messagelist

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fastcampusbasic.Part2.chapter6.userlist.UserItem
import com.example.fastcampusbasic.databinding.ItemMessageBinding

class MessageAdapter : ListAdapter<MessageItem, MessageAdapter.MessageViewHolder>(diffUtil) {

    var otherUserItem: UserItem? = null

    inner class MessageViewHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MessageItem) {
            if (item.userId == otherUserItem?.userId) {
                binding.usernameTv.isVisible = true
                binding.usernameTv.text = otherUserItem?.username
                binding.messageTv.text = item.message
                binding.messageTv.gravity = Gravity.START
            } else {
                binding.usernameTv.isVisible = false
                binding.messageTv.text = item.message
                binding.messageTv.gravity = Gravity.END
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.MessageViewHolder {
        return MessageViewHolder(ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MessageAdapter.MessageViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<MessageItem>() {
            override fun areItemsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
                return oldItem.messageId == newItem.messageId
            }

            override fun areContentsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}