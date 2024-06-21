package com.example.fastcampusbasic.Part2.chapter12.player

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fastcampusbasic.R
import com.example.fastcampusbasic.databinding.ItemHeaderBinding
import com.example.fastcampusbasic.databinding.ItemVideoBinding

class PlayerVideoAdapter(
    private val context: Context,
    private val onClick: (PlayerVideo) -> Unit
) :
    ListAdapter<PlayerVideoModel, RecyclerView.ViewHolder>(diffUtil) {

    inner class HeaderViewHolder(private val binding: ItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlayerHeader) {
            binding.titleTv.text = item.title
            binding.subTitleTv.text = context.getString(
                R.string.header_sub_title_video_info,
                item.viewCount,
                item.dateText
            )
            binding.channelNameTv.text = item.channelName

            Glide.with(binding.channelLogoIv)
                .load(item.channelThumb)
                .circleCrop()
                .into(binding.channelLogoIv)
        }
    }

    inner class VideoViewHolder(private val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlayerVideo) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            HeaderViewHolder(
                ItemHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            VideoViewHolder(
                ItemVideoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            (holder as HeaderViewHolder).bind(currentList[position] as PlayerHeader)
        } else {
            (holder as VideoViewHolder).bind(currentList[position] as PlayerVideo)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_VIDEO
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_VIDEO = 1

        val diffUtil = object : DiffUtil.ItemCallback<PlayerVideoModel>() {
            override fun areItemsTheSame(
                oldItem: PlayerVideoModel,
                newItem: PlayerVideoModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: PlayerVideoModel,
                newItem: PlayerVideoModel
            ): Boolean {
                return if (oldItem is PlayerVideo && newItem is PlayerVideo) {
                    oldItem == newItem
                } else if (oldItem is PlayerHeader && newItem is PlayerHeader) {
                    oldItem == newItem
                } else {
                    false
                }
            }
        }
    }
}