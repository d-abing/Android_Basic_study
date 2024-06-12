package com.example.fastcampusbasic.Part2.chapter6.userlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fastcampusbasic.databinding.ItemChatUserBinding

class ChatUserAdapter(private val onClick: (UserItem) -> Unit) : ListAdapter<UserItem, ChatUserAdapter.ChatUserViewHolder>(diffUtil) {

    inner class ChatUserViewHolder(private val binding: ItemChatUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserItem) {
            binding.nicknameTv.text = item.username
            binding.descriptionTv.text = item.description
            binding.descriptionTv.isVisible = binding.descriptionTv.text.isNotEmpty()

            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserAdapter.ChatUserViewHolder {
        return ChatUserViewHolder(ItemChatUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ChatUserAdapter.ChatUserViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<UserItem>() {
            override fun areItemsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}