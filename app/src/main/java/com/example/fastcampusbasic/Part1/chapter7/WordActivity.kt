package com.example.fastcampusbasic.Part1.chapter7

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fastcampusbasic.databinding.ActivityWordBinding

class WordActivity : AppCompatActivity(), WordAdapter.ItemClickListener {
    private lateinit var binding: ActivityWordBinding
    private lateinit var wordAdapter: WordAdapter
    private var selectedWord: Word? = null
    private val updateAddWordResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val isUpdated = result.data?.getBooleanExtra("isUpdated", false) ?: false
            if (result.resultCode == RESULT_OK && isUpdated) {
                updateAddWord()
            }
        }

    private val updateEditWordResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val editWord = result.data?.getParcelableExtra<Word>("editWord")
            if (result.resultCode == RESULT_OK && editWord != null) {
                updateEditWord(editWord)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        binding.addBtn.setOnClickListener {
            Intent(this, AddActivity::class.java).let {
                updateAddWordResult.launch(it)
            }
        }

        binding.deleteWordIv.setOnClickListener {
            delete()
        }

        binding.editWordIv.setOnClickListener {
            edit()
        }
    }

    private fun initRecyclerView() {
        wordAdapter = WordAdapter(mutableListOf(), this)
        binding.wordRv.apply {
            adapter = wordAdapter
            layoutManager = LinearLayoutManager(applicationContext)
            val dividerItemDecoration =
                DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL)
            addItemDecoration(dividerItemDecoration)
        }

        Thread {
            val list = AppDatabase.getInstance(this)?.wordDao()?.getAll() ?: emptyList()
            wordAdapter.list.addAll(list)
            runOnUiThread { wordAdapter.notifyDataSetChanged() }
        }.start()
    }

    private fun updateAddWord() {
        Thread {
            AppDatabase.getInstance(this)?.wordDao()?.getLatestWord()?.let { word ->
                wordAdapter.list.add(0, word)
                runOnUiThread { wordAdapter.notifyDataSetChanged() }
            }
        }.start()
    }

    private fun updateEditWord(word: Word) {
        val index = wordAdapter.list.indexOfFirst { it.id == word.id }
        wordAdapter.list[index] = word
        runOnUiThread {
            selectedWord = word
            wordAdapter.notifyItemChanged(index)
            binding.textTv.text = word.text
            binding.meanTv.text = word.mean
        }
    }

    private fun delete() {
        if (selectedWord == null) return

        Thread {
            selectedWord?.let { word ->
                AppDatabase.getInstance(this)?.wordDao()?.delete(word)
                runOnUiThread {
                    wordAdapter.list.remove(word)
                    wordAdapter.notifyDataSetChanged()
                    binding.textTv.text = ""
                    binding.meanTv.text = ""
                    Toast.makeText(this, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun edit() {
        if (selectedWord == null) return

        val intent = Intent(this, AddActivity::class.java).putExtra("originalWord", selectedWord)
        updateEditWordResult.launch(intent)
    }

    override fun onClick(word: Word) {
        selectedWord = word
        binding.textTv.text = word.text
        binding.meanTv.text = word.mean
    }
}