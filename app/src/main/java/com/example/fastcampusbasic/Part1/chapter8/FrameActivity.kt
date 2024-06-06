package com.example.fastcampusbasic.Part1.chapter8

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.fastcampusbasic.databinding.ActivityFrameBinding
import com.google.android.material.tabs.TabLayoutMediator

class FrameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFrameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFrameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.apply {
            title = "나만의 앨범"
            setSupportActionBar(this)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val images = (intent.getStringArrayExtra("images") ?: emptyArray())
            .map { uriString -> FrameItem(Uri.parse(uriString)) }

        val frameAdapter = FrameAdapter(images)

        binding.viewPager.adapter = frameAdapter

        TabLayoutMediator(
            binding.tabL,
            binding.viewPager,
        ) {
            tab, _ ->
            binding.viewPager.currentItem = tab.position
        }.attach()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}