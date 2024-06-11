package com.example.fastcampusbasic.Part2.chapter4.network

import com.example.fastcampusbasic.Part2.chapter4.model.Repo
import com.example.fastcampusbasic.Part2.chapter4.model.UserDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubService {
    @GET("/users/{username}/repos")
    fun listRepos(@Path("username") username: String, @Query("page") page: Int): Call<List<Repo>>

    @GET("search/users")
    fun searchUsers(@Query("q") q: String): Call<UserDto>
}