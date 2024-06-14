package com.example.fastcampusbasic.Part2.chapter7

import com.google.gson.annotations.SerializedName

data class WeatherEntity(
    @SerializedName("response")
    val response: WeatherResponse
)

data class WeatherResponse(
    @SerializedName("header")
    val header: WeatherHeader,
    @SerializedName("body")
    val body: WeatherBody,
)

data class WeatherHeader(
    @SerializedName("resultCode")
    val resultCode: String,
    @SerializedName("resultMsg")
    val resultMsg: String,
)

data class WeatherBody(
    @SerializedName("items")
    val items: ForecastEntityList,
)

data class ForecastEntityList(
    @SerializedName("item")
    val forecastEntities: List<ForecastEntity>,
)

data class ForecastEntity(
    @SerializedName("baseDate")
    val baseDate: String,
    @SerializedName("baseTime")
    val baseTime: String,
    @SerializedName("category")
    val category: Category?,
    @SerializedName("fcstDate")
    val fcstDate: String,
    @SerializedName("fcstTime")
    val fcstTime: String,
    @SerializedName("fcstValue")
    val fcstValue: String,
    @SerializedName("nx")
    val nx: Int,
    @SerializedName("ny")
    val ny: Int,
)

