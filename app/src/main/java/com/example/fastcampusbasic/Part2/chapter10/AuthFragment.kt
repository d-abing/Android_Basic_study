package com.example.fastcampusbasic.Part2.chapter10

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.fastcampusbasic.R
import com.example.fastcampusbasic.databinding.ActivityHouseBinding
import com.example.fastcampusbasic.databinding.FragmentAuthBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class AuthFragment : Fragment(R.layout.fragment_auth) {
    private lateinit var binding: FragmentAuthBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAuthBinding.bind(view)

        setupSignUpButton()
        setupSignInButton()
    }

    override fun onStart() {
        super.onStart()

        if (Firebase.auth.currentUser == null) {
            initViewToSignOutState()
        } else {
            initViewToSignInState()
        }
    }

    private fun setupSignUpButton() {
        binding.signUpBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(binding.root, "이메일과 비밀번호를 입력해주세요.", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Snackbar.make(binding.root, "회원가입에 성공했습니다. 자동으로 로그인 되었습니다.", Snackbar.LENGTH_SHORT).show()
                        initViewToSignInState()
                    } else {
                        Snackbar.make(binding.root, "회원가입에 실패했습니다.", Snackbar.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun setupSignInButton() {
        binding.signInOutBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()

            if (Firebase.auth.currentUser == null) {
                // 로그인
                if (email.isEmpty() || password.isEmpty()) {
                    Snackbar.make(binding.root, "이메일과 비밀번호를 입력해주세요.", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                Firebase.auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            initViewToSignInState()
                        } else {
                            Snackbar.make(binding.root, "로그인에 실패했습니다.", Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    }
            } else {
                // 로그아웃
                Firebase.auth.signOut()
                initViewToSignOutState()
            }
        }
    }

    private fun initViewToSignInState() {
        binding.emailEt.setText(Firebase.auth.currentUser?.email)
        binding.emailEt.isEnabled = false
        binding.passwordEt.isVisible = false
        binding.signUpBtn.isEnabled = false
        binding.signInOutBtn.text = getString(R.string.signOut)
    }

    private fun initViewToSignOutState() {
        binding.emailEt.text.clear()
        binding.passwordEt.text.clear()
        binding.emailEt.isEnabled = true
        binding.passwordEt.isVisible = true
        binding.signUpBtn.isEnabled = true
        binding.signInOutBtn.text = getString(R.string.signIn)
    }

}