package com.example.fastcampusbasic.Part2.chapter4

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fastcampusbasic.Part2.chapter4.APIClient.retrofit
import com.example.fastcampusbasic.Part2.chapter4.adapter.UserAdapter
import com.example.fastcampusbasic.Part2.chapter4.model.UserDto
import com.example.fastcampusbasic.Part2.chapter4.network.GithubService
import com.example.fastcampusbasic.databinding.ActivityGithubRepBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GithubRepActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGithubRepBinding
    private lateinit var userAdapter: UserAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var searchFor: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGithubRepBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userAdapter = UserAdapter {
            val intent = Intent(this@GithubRepActivity, RepoActivity::class.java)
            intent.putExtra("username", it.username)
            startActivity(intent)
        }

        binding.userRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }

        val runnable = Runnable {
            searchUser()
        }

        binding.searchEt.addTextChangedListener {

            searchFor = it.toString()
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, 300)

        }
    }

    private fun searchUser() {
        val githubService = retrofit.create(GithubService::class.java)
        githubService.searchUsers(searchFor).enqueue(object: Callback<UserDto> {
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                Log.e("GithubRepoActivity", "Search User : ${response.body().toString()}")

                userAdapter.submitList(response.body()?.items)
            }

            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                Toast.makeText(this@GithubRepActivity, "에러가 발생했습니다.", Toast.LENGTH_SHORT).show()
                t.printStackTrace()
            }
        })
    }
}