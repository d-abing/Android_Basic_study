package com.example.fastcampusbasic.Part2.chapter1

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fastcampusbasic.databinding.ActivityWebtoonBinding
import com.google.android.material.tabs.TabLayoutMediator

class WebtoonActivity : AppCompatActivity(), OnTabLayoutNameChanged {
    private lateinit var binding: ActivityWebtoonBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebtoonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreference = getSharedPreferences(WebViewFragment.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        val tab0 = sharedPreference?.getString("tab0_name", "월요 웹툰")
        val tab1 = sharedPreference?.getString("tab1_name", "화요 웹툰")
        val tab2 = sharedPreference?.getString("tab2_name", "수요 웹툰")

        binding.viewPager.adapter = WebtoonAdapter(this)

        TabLayoutMediator(binding.tabL, binding.viewPager) { tab, position ->
            run {
                tab.text = when(position) {
                    0 -> tab0
                    1 -> tab1
                    else -> tab2
                }
            }
        }.attach()
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.fragments[binding.viewPager.currentItem]
        if(currentFragment is WebViewFragment) {
            if(currentFragment.canGoBack()) {
                currentFragment.goBack()
            } else {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun nameChanged(position: Int, name: String) {
        val tab = binding.tabL.getTabAt(position)
        tab?.text = name
    }
}