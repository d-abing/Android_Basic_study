package com.example.fastcampusbasic.Part2.chapter9

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fastcampusbasic.R
import com.example.fastcampusbasic.databinding.ActivityEmailLoginBinding

class EmailLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmailLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.emailLoginBtn.setOnClickListener {
            if (binding.emailEt.text.isNotEmpty()) {
                val data = Intent().apply {
                    putExtra("email", binding.emailEt.text.toString())
                }
                setResult(RESULT_OK, data)
                finish()
            } else {
                Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}