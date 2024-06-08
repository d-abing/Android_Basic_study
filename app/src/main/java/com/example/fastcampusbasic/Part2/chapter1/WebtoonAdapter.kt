package com.example.fastcampusbasic.Part2.chapter1

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class WebtoonAdapter(private val webtoonActivity: WebtoonActivity) : FragmentStateAdapter(webtoonActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {
                return WebViewFragment(position, "https://comic.네이버.com/webtoon?tab=mon").apply {
                    listener = webtoonActivity
                }
            }
            1 -> {
                return WebViewFragment(position, "https://comic.네이버.com/webtoon?tab=tue").apply {
                    listener = webtoonActivity
                }
            }
            else -> {
                return WebViewFragment(position, "https://comic.네이버.com/webtoon?tab=wed").apply {
                    listener = webtoonActivity
                }
            }
        }
    }
}