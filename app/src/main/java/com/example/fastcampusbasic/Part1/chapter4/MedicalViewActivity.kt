package com.example.fastcampusbasic.Part1.chapter4

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.fastcampusbasic.databinding.ActivityMedicalViewBinding

class MedicalViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMedicalViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicalViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goEditBtn.setOnClickListener {
            val intent = Intent(this, MedicalEditActivity::class.java)
            startActivity(intent)
        }

        binding.deleteBtn.setOnClickListener {
            deleteData()
        }

        binding.phoneLayer.setOnClickListener {
            with(Intent(Intent.ACTION_VIEW)) {
                val phoneNumber = binding.phoneValueTv.text.toString()
                    .replace("-", "")
                data = Uri.parse("tel: $phoneNumber")
                startActivity(this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getDataAndUiUpdate()
    }

    private fun deleteData() {
        with(getSharedPreferences(USER_INFORMATION, Context.MODE_PRIVATE).edit()) {
            clear()
            apply()
            getDataAndUiUpdate()
        }
        Toast.makeText(this, "초기화를 완료했습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun getDataAndUiUpdate() {
        with(getSharedPreferences(USER_INFORMATION, Context.MODE_PRIVATE)) {
            binding.nameValueTv.text = getString(NAME, "미정")
            binding.birthdateValueTv.text = getString(BIRTHDATE, "미정")
            binding.bloodValueTv.text = getString(BLOOD_TYPE, "미정")
            binding.phoneValueTv.text = getString(PHONE, "미정")
            val warning = getString(WARNING, "")
            binding.warningTv.isVisible = !warning.isNullOrEmpty()
            binding.warningValueTv.isVisible = !warning.isNullOrEmpty()
            if (!warning.isNullOrEmpty()) {
                binding.warningValueTv.text = warning
            }
        }
    }
}