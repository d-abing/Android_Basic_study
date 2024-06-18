package com.example.fastcampusbasic.Part2.chapter9

import android.content.Intent
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.fastcampusbasic.R
import com.example.fastcampusbasic.databinding.ActivityKakaoLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User

class KakaoLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKakaoLoginBinding
    private lateinit var emailLoginResult: ActivityResultLauncher<Intent>
    private lateinit var pendingUser: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKakaoLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        KakaoSdk.init(this, getString(R.string.kakao_native_app_key))

        if (Firebase.auth.currentUser != null) {
            navigateToWhereActivity()
        }

        emailLoginResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val email = it.data?.getStringExtra("email")
                if (email == null) {
                    showErrorToast()
                    return@registerForActivityResult
                } else {
                    signInFirebase(pendingUser, email)
                }
            }
        }

        binding.kakaoLoginBtn.setOnClickListener {

            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    showErrorToast()
                    error.printStackTrace()
                } else if (token != null) {
                    getKakaoAccountInfo()
                }
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)

                    } else if (token != null) {
                        if (Firebase.auth.currentUser == null) {
                            getKakaoAccountInfo()
                        } else {
                            navigateToWhereActivity()
                        }
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
    }

    private fun showErrorToast() {
        Toast.makeText(this, "사용자 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun getKakaoAccountInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                showErrorToast()
                error.printStackTrace()
            } else if (user != null) {
                //사용자 정보 요청 성공
                checkKakaoUserData(user)
            }
        }
    }

    private fun checkKakaoUserData(user: User) {
        val kakaoEmail = user.kakaoAccount?.email.orEmpty()

        if (kakaoEmail.isEmpty()) {
            // 추가로 이메일을 받는 작업
            pendingUser = user
            emailLoginResult.launch(Intent(this, EmailLoginActivity::class.java))

            return
        }
        signInFirebase(user, kakaoEmail)
    }

    private fun signInFirebase(user: User, email: String) {
        val uId = user.id.toString()
        Firebase.auth.createUserWithEmailAndPassword(email, uId).addOnCompleteListener {
            if (it.isSuccessful) {
                updateFirebaseDatabase(user)
            }
        }.addOnFailureListener {
            // 이미 가입된 계정
            if (it is FirebaseAuthUserCollisionException) {
                Firebase.auth.signInWithEmailAndPassword(email, uId).addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        updateFirebaseDatabase(user)
                    } else {
                        showErrorToast()
                    }
                }.addOnFailureListener { error ->
                    error.printStackTrace()
                    showErrorToast()
                }
            } else {
                showErrorToast()
            }
        }
    }

    private fun updateFirebaseDatabase(user: User) {
        val uid = Firebase.auth.currentUser?.uid.orEmpty()

        val personMap = mutableMapOf<String, Any>()
        personMap["uid"] = uid
        personMap["name"] = user.kakaoAccount?.profile?.nickname.orEmpty()
        personMap["profilePhoto"] = user.kakaoAccount?.profile?.thumbnailImageUrl.orEmpty()

        Firebase.database.reference.child("Person").child(uid).updateChildren(personMap)
        navigateToWhereActivity()
    }

    private fun navigateToWhereActivity() {
        startActivity(Intent(this, WhereActivity::class.java))
    }
}