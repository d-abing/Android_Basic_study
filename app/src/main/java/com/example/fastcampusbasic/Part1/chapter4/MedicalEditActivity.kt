package com.example.fastcampusbasic.Part1.chapter4

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.fastcampusbasic.R
import com.example.fastcampusbasic.databinding.ActivityMedicalEditBinding
import java.time.LocalDate

class MedicalEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMedicalEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicalEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bloodtypeSp.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.bloodtype,
            android.R.layout.simple_list_item_1
        )

        binding.birthdateLayer.setOnClickListener {
            val listener = OnDateSetListener { _, year, month, dayOfMonth ->
                binding.birthdateValueTv.text = "$year-${month.inc()}-$dayOfMonth"
            }
            val currentDate = LocalDate.now()
            DatePickerDialog(
                this,
                listener,
                currentDate.year,
                currentDate.monthValue.dec(),
                currentDate.dayOfMonth,
            ).show()
        }

        binding.warningCb.setOnCheckedChangeListener { _, isChecked ->
            binding.warningEt.isVisible = isChecked
        }

        binding.warningEt.isVisible = binding.warningCb.isChecked

        binding.saveBtn.setOnClickListener {
            saveData()
            finish()
        }
    }

    private fun saveData() {
        with(getSharedPreferences(USER_INFORMATION, Context.MODE_PRIVATE).edit()) {
            putString(NAME, binding.nameEt.text.toString())
            putString(BIRTHDATE, binding.birthdateValueTv.text.toString())
            putString(BLOOD_TYPE, getBloodType())
            putString(PHONE, binding.phoneEt.text.toString())
            putString(WARNING, getWarning())
            apply()
        }

        Toast.makeText(this, "저장을 완료했습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun getBloodType(): String {
        val bloodSign = if (binding.bloodtypePlusRb.isChecked) "+" else "-"
        val bloodAlphabet = binding.bloodtypeSp.selectedItem.toString()
        return "$bloodSign$bloodAlphabet"
    }

    private fun getWarning(): String {
        return if (binding.warningCb.isChecked) binding.warningEt.text.toString() else ""
    }
}