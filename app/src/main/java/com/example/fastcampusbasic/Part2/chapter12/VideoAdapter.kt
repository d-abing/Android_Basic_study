package com.example.fastcampusbasic.Part2.chapter12

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fastcampusbasic.R
import com.example.fastcampusbasic.databinding.ItemVideoBinding

class VideoAdapter(
    private val context: Context,
    private val onClick: (VideoEntity) -> Unit
) :
    ListAdapter<VideoEntity, VideoAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(
        private val binding: ItemVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VideoEntity) {
            binding.titleTv.text = item.title
            binding.subTitleTv.text = context.getString(
                R.string.sub_title_video_info,
                item.channelName,
                item.viewCount,
                item.dateText
            )

            Glide.with(binding.videoThumbnailIv)
                .load(item.videoThumb)
                .into(binding.videoThumbnailIv)

            Glide.with(binding.channelLogoIv)
                .load(item.channelThumb)
                .circleCrop()
                .into(binding.channelLogoIv)

            binding.root.setOnClickListener {
                onClick.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemVideoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<VideoEntity>() {
            override fun areItemsTheSame(oldItem: VideoEntity, newItem: VideoEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: VideoEntity, newItem: VideoEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}