package com.example.fastcampusbasic.Part2.chapter10.ui.house

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fastcampusbasic.R
import com.example.fastcampusbasic.databinding.ItemArticleBinding

class HouseArticleAdapter(val onItemClicked: (ArticleItem) -> Unit, val onBookmarkClicked: (String, Boolean) -> Unit) : ListAdapter<ArticleItem, HouseArticleAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(article: ArticleItem) {
                Glide.with(binding.thumbnailIv)
                    .load(article.imageUrl)
                    .into(binding.thumbnailIv)
                binding.descriptionTv.text = article.description

                if (article.isBookmark) {
                    binding.bookmarkIb.setBackgroundResource(R.drawable.baseline_bookmark_24)
                } else {
                    binding.bookmarkIb.setBackgroundResource(R.drawable.baseline_bookmark_border_24)
                }

                binding.bookmarkIb.setOnClickListener {
                    onBookmarkClicked.invoke(article.articleId, article.isBookmark.not())

                    article.isBookmark = article.isBookmark.not()
                    if (article.isBookmark) {
                        binding.bookmarkIb.setBackgroundResource(R.drawable.baseline_bookmark_24)
                    } else {
                        binding.bookmarkIb.setBackgroundResource(R.drawable.baseline_bookmark_border_24)
                    }
                }

                binding.root.setOnClickListener {
                    onItemClicked(article)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemArticleBinding.inflate(
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
        val diffUtil = object : DiffUtil.ItemCallback<ArticleItem>() {
            override fun areItemsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean {
                return oldItem.articleId == newItem.articleId
            }

            override fun areContentsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean {
                return oldItem == newItem
            }
        }
    }

}