package com.example.fastcampusbasic.Part2.chapter11

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.fastcampusbasic.R
import com.example.fastcampusbasic.databinding.ActivityCafeBinding

class CafeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCafeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCafeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNv.setupWithNavController(navController)
    }
}