package com.example.fastcampusbasic.Part2.chapter4.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fastcampusbasic.Part2.chapter4.model.Repo
import com.example.fastcampusbasic.databinding.ItemRepoBinding

class RepoAdapter(private val onClick: (Repo) -> Unit): ListAdapter<Repo, RepoAdapter.RepoViewHolder>(diffUtil) {

    inner class RepoViewHolder(private val binding: ItemRepoBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Repo) {
            binding.reponameTv.text = item.name
            binding.descriptionTv.text = item.description
            binding.starCountTv.text = item.stars.toString()
            binding.forkCountTv.text = item.forks.toString()

            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoAdapter.RepoViewHolder {
        return RepoViewHolder(
            ItemRepoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RepoAdapter.RepoViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Repo>() {
            override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem == newItem
            }
        }
    }


}