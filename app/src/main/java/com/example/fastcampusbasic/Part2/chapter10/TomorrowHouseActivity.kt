package com.example.fastcampusbasic.Part2.chapter10

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.fastcampusbasic.R
import com.example.fastcampusbasic.databinding.ActivityHouseBinding

class TomorrowHouseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHouseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHouseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding.bottomNv.setupWithNavController(navHostFragment.navController)
    }
}