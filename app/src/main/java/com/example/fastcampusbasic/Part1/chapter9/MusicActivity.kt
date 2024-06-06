package com.example.fastcampusbasic.Part1.chapter9

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fastcampusbasic.databinding.ActivityMusicBinding

class MusicActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMusicBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playBtn.setOnClickListener { mediaPlayerPlay() }
        binding.pauseBtn.setOnClickListener { mediaPlayerPause() }
        binding.stopBtn.setOnClickListener { mediaPlayerStop() }
    }

    private fun mediaPlayerPlay() {
        val intent = Intent(this, MediaPlayerService::class.java)
            .apply { action = MEDIA_PLAYER_PLAY }
        startService(intent)
    }

    private fun mediaPlayerPause() {
        val intent = Intent(this, MediaPlayerService::class.java)
            .apply { action = MEDIA_PLAYER_PAUSE }
        startService(intent)
    }

    private fun mediaPlayerStop() {
        val intent = Intent(this, MediaPlayerService::class.java)
            .apply { action = MEDIA_PLAYER_STOP }
        startService(intent)
    }

    override fun onDestroy() {
        stopService(Intent(this, MediaPlayerService::class.java))
        super.onDestroy()
    }
}