package com.example.fastcampusbasic.Part1.chapter3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.fastcampusbasic.databinding.ActivityConverterBinding
import java.math.BigDecimal
import java.math.RoundingMode

class ConverterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConverterBinding
    private var cmToM = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConverterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cmToM = savedInstanceState?.getBoolean("cmToM") ?: true
        setUnit()

        var inputNumber = 0
        binding.inputEt.addTextChangedListener { text ->
            inputNumber = if (text.isNullOrEmpty()) {
                0
            } else {
                text.toString().toInt()
            }
            setOutput(inputNumber)
        }

        binding.swapIb.setOnClickListener {
            cmToM = !cmToM
            setUnit()
            setOutput(inputNumber)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("cmToM", cmToM)
        super.onSaveInstanceState(outState)
    }

    private fun setUnit() {
        binding.inputUnitTv.text = if (cmToM) "cm" else "m"
        binding.outputUnitTv.text = if (cmToM) "m" else "cm"
    }

    private fun setOutput(inputNumber: Int) {
        if (cmToM) {
            binding.outputTv.text = cmToMConvert(inputNumber)
        } else {
            binding.outputTv.text = mToCmConvert(inputNumber)
        }
    }

    private fun cmToMConvert(cm: Int): String {
        val number = BigDecimal(cm)
        val times = BigDecimal("0.01")
        return number.multiply(times).setScale(2, RoundingMode.HALF_UP).toString()
    }

    private fun mToCmConvert(m: Int): String {
        return m.times(100).toString()
    }
}

