package com.example.fastcampusbasic.Part2.chapter8

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fastcampusbasic.databinding.ItemRestaurantBinding
import com.naver.maps.geometry.LatLng

class SearchResultAdapter(private val onClick: (LatLng) -> Unit): RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    private var dataSet = emptyList<SearchItem>()

    inner class ViewHolder(private val binding: ItemRestaurantBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchItem) {
            binding.titleTv.text = item.title
            binding.categoryTv.text = item.category
            binding.locationTv.text = item.roadAddress

            binding.root.setOnClickListener{
                onClick(LatLng(item.mapy / 1e7, item.mapx / 1e7))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setDataSet(dataSet: List<SearchItem>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }
}