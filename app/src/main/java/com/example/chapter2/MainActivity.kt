package com.example.chapter2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import com.example.chapter2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var number = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        number = savedInstanceState?.getInt("number") ?: 0

        binding.numberTv.text = number.toString()

        binding.resetBtn.setOnClickListener{
            number = 0
            binding.numberTv.text = number.toString()
        }

        binding.plusBtn.setOnClickListener {
            number++
            binding.numberTv.text = number.toString()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("number", number)
    }
}
