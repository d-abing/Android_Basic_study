package com.example.fastcampusbasic.Part1.chapter2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fastcampusbasic.databinding.ActivityCounterBinding

class CounterActivity : AppCompatActivity() {
    private var number = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCounterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        number = savedInstanceState?.getInt("number") ?: 0

        binding.numberTv.text = number.toString()

        binding.resetBtn.setOnClickListener {
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
