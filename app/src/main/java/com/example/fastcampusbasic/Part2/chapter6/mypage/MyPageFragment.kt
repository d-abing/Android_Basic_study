package com.example.fastcampusbasic.Part2.chapter6.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fastcampusbasic.Part2.chapter6.Key
import com.example.fastcampusbasic.Part2.chapter6.LoginActivity
import com.example.fastcampusbasic.Part2.chapter6.userlist.UserItem
import com.example.fastcampusbasic.R
import com.example.fastcampusbasic.databinding.FragmentMyPageBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database

class MyPageFragment : Fragment(R.layout.fragment_my_page) {

    private lateinit var binding: FragmentMyPageBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMyPageBinding.bind(view)

        val currentUserId = Firebase.auth.currentUser?.uid ?: ""
        val userDB = Firebase.database.reference.child(Key.DB_USERS).child(currentUserId)

        userDB.get().addOnSuccessListener {
            val currentUserItem = it.getValue(UserItem::class.java) ?: return@addOnSuccessListener
            binding.usernameEt.setText(currentUserItem.username)
            binding.descriptionEt.setText(currentUserItem.description)
        }

        binding.applyBtn.setOnClickListener {
            val username = binding.usernameEt.text.toString()
            val description = binding.descriptionEt.text.toString()

            if (username.isEmpty()) {
                Toast.makeText(context, "유저 이름은 빈 값으로 설정할 수 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = mutableMapOf<String, Any>()
            user["username"] = username
            user["description"] = description
            userDB.updateChildren(user)
        }

        binding.signOutBtn.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
        }
    }
}