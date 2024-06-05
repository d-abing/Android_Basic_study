package com.example.fastcampusbasic.Part1.chapter7

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import com.example.fastcampusbasic.databinding.ActivityAddBinding
import com.google.android.material.chip.Chip

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private var originWord: Word? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        binding.addBtn.setOnClickListener {
            if (originWord == null) {
                add()
            } else {
                edit()
            }
        }
    }

    private fun initViews() {
        val types = listOf(
            "명사", "동사", "대명사", "형용사", "부사", "감탄사", "전치사", "접속사"
        )
        binding.typeChipG.apply {
            types.forEach { text ->
                addView(createChip(text))
            }
        }

        binding.textInputEt.addTextChangedListener {
            it?.let { text ->
                binding.textInputL.error = when (text.length) {
                    0 -> "값을 입력해주세요"
                    1 -> "2자 이상을 입력해주세요"
                    else -> null
                }
            }
        }

        originWord = intent.getParcelableExtra("originalWord")
        originWord?.let { word ->
            binding.textInputEt.setText(word.text)
            binding.meanInputEt.setText(word.mean)
            val selectedChip =
                binding.typeChipG.children.firstOrNull { (it as Chip).text == word.type } as? Chip
            selectedChip?.isChecked = true
        }
    }

    private fun createChip(text: String): Chip {
        return Chip(this).apply {
            setText(text)
            isCheckable = true
            isClickable = true
        }
    }

    private fun add() {
        val text = binding.textInputEt.text.toString()
        val mean = binding.meanInputEt.text.toString()
        val type = findViewById<Chip>(binding.typeChipG.checkedChipId).text.toString()
        val word = Word(text, mean, type)

        Thread {
            AppDatabase.getInstance(this)?.wordDao()?.insert(word)
            runOnUiThread {
                Toast.makeText(this, "저장을 완료했습니다.", Toast.LENGTH_SHORT).show()
            }
            val intent = Intent().putExtra("isUpdated", true)
            setResult(RESULT_OK, intent)
            finish()
        }.start()
    }

    private fun edit() {
        val text = binding.textInputEt.text.toString()
        val mean = binding.meanInputEt.text.toString()
        val type = findViewById<Chip>(binding.typeChipG.checkedChipId).text.toString()
        val editWord = originWord?.copy(text = text, mean = mean, type = type)

        Thread {
            editWord?.let { word ->
                AppDatabase.getInstance(this)?.wordDao()?.update(word)
                runOnUiThread {
                    Toast.makeText(this, "수정을 완료했습니다.", Toast.LENGTH_SHORT).show()
                }
                val intent = Intent().putExtra("editWord", word)
                setResult(RESULT_OK, intent)
                finish()
            }
        }.start()
    }
}