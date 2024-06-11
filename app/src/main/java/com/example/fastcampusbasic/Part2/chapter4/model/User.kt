package com.example.fastcampusbasic.Part2.chapter4.model

import com.google.gson.annotations.SerializedName

data class User (
    @SerializedName("id")
    val id: Int,
    @SerializedName("login")
    val username: String,
)